package com.app.accgt.groupify.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.accgt.groupify.R;
import com.app.accgt.groupify.models.Event;
import com.app.accgt.groupify.utils.EventHolder;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FeedActivity extends AppCompatActivity {
    private static final String TAG = FeedActivity.class.getSimpleName();

    private Context context;
    private DrawerLayout drawerLayout;
    private FirebaseUser fbUser;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton addEventButton = findViewById(R.id.add_event_button);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        NavigationView navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        View navHeader = navigationView.getHeaderView(0);
        MenuItem verifyEmailItem = navigationView.getMenu().findItem(R.id.verify_email);
        TextView userEmail = navHeader.findViewById(R.id.user_email);
        ImageView verifiedCheckMark = navHeader.findViewById(R.id.verified_check_mark);
        TextView userFirstLastName = navHeader.findViewById(R.id.user_first_last_name);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fbUser != null) {
            userEmail.setText(fbUser.getEmail());
            userFirstLastName.setText(fbUser.getDisplayName());

            if (fbUser.isEmailVerified()) {
                verifyEmailItem.setVisible(false);
                verifiedCheckMark.setVisibility(View.VISIBLE);
            } else {
                verifyEmailItem.setVisible(true);
                verifiedCheckMark.setVisibility(View.GONE);
            }
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        Query query = FirebaseFirestore.getInstance()
                .collection("events")
                .orderBy("timestamp");
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        Log.d(TAG, "Creating adapter");
        adapter = new FirestoreRecyclerAdapter<Event, EventHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EventHolder holder, final int position, @NonNull Event model) {
                holder.name.setText(model.getName());
                String participantsText = String.valueOf(model.getUsers().size() + " people");
                holder.participantsNumber.setText(participantsText);
                String durationText = String.valueOf(model.getDuration()) + " minutes";
                holder.duration.setText(durationText);
                holder.location.setText(model.getLocation().getName());
                holder.time.setText(model.getTime().toString());

                holder.eventItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, EventActivity.class);
                        String eventId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                        intent.putExtra("eventId", eventId);
                        startActivity(intent);
                    }
                });

                Log.d(TAG, "Bound event " + model.getName());
            }

            @NonNull
            @Override
            public EventHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item, viewGroup, false);
                Log.d(TAG, "Created EventHolder");
                return new EventHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddEventActivity.class));
            }
        });

        navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLACK));
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sign_out:
                                signOut();
                                break;
                            case R.id.verify_email:
                                verifyEmail();
                                break;
                            case R.id.request_password_reset:
                                requestPasswordReset();
                                break;
                            case R.id.delete_user:
                                deleteUser();
                                break;
                        }
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        drawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void signOut() {
        AuthUI.getInstance().signOut(context).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // user is now signed out
                // do something (open a new activity)
                // ...
                if (task.isSuccessful()) {
                    Log.d(TAG, "User signed out");
                    startActivity(new Intent(context, MainActivity.class));

                    finish();
                }
            }
        });
    }

    private void verifyEmail() {
        fbUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");

                    Toast.makeText(context, "Email sent. Log in again to apply changes.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void requestPasswordReset() {
        if (fbUser.getEmail() != null) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(fbUser.getEmail())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");

                                Toast.makeText(context, "Email sent.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }

    private void deleteUser() {
        fbUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User account deleted");
                    startActivity(new Intent(context, MainActivity.class));

                    finish();
                }
            }
        });
    }
}
