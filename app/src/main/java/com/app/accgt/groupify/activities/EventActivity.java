package com.app.accgt.groupify.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.accgt.groupify.R;
import com.app.accgt.groupify.models.Event;
import com.app.accgt.groupify.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;

public class EventActivity extends AppCompatActivity {
    private static final String TAG = EventActivity.class.getSimpleName();

    private Event event;

    private TextView eventDescription;
    private TextView eventDuration;
    private TextView eventLocation;
    private TextView eventTime;
    private TextView eventTimeStamp;
    private Button leaveJoinButton;
    private Map<String, Object> userMap = new HashMap<>();

    private FirebaseUser fbUser;
    private DocumentReference docRef;
    private User user;

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_event);

        context = this;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        eventDescription = findViewById(R.id.event_description);
        eventDuration = findViewById(R.id.event_duration);
        eventLocation = findViewById(R.id.event_location);
        eventTime = findViewById(R.id.event_time);
        eventTimeStamp = findViewById(R.id.event_time_stamp);
        leaveJoinButton = findViewById(R.id.leave_join_button);

        final String eventId = getIntent().getStringExtra("eventId");
        docRef = FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventId);
        docRef.get(Source.CACHE).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        event = document.toObject(Event.class);
                        populateEvent();
                        setButtonListeners();
                        Log.d(TAG, "Cached document data " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });
    }

    private void populateEvent() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(event.getName());
        }

        eventDescription.setText(event.getDescription());
        eventDuration.setText(String.valueOf(event.getDuration()));
        eventLocation.setText(event.getLocation().getName());
        eventTime.setText(event.getTime().toString());
        eventTimeStamp.setText(event.getTimestamp().toString());
    }

    private void setButtonListeners() {
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {

            if (event.getUsers() != null) {
                user = event.getUsers().stream().filter(u -> fbUser.getUid().equals(u.getUid())).findFirst().orElse(null);
            }

            if (user != null) {
                leaveJoinButton.setText("Leave");
            } else {
                leaveJoinButton.setText("Join");
            }

            leaveJoinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (leaveJoinButton.getText().toString()) {
                        case "Leave":
                            // Removes users to the FireCore database, adhering to the map field it creates
                            if (fbUser.getDisplayName() != null) {
                                userMap.put("name", fbUser.getDisplayName());
                            }
                            userMap.put("uid", fbUser.getUid());
                            docRef.update("users", FieldValue.arrayRemove(userMap));

                            startActivity(new Intent(context, FeedActivity.class));
                            finish();
                            break;
                        case "Join":
                            // Adds users to the FireCore database, adhering to the map field it creates
                            if (fbUser.getDisplayName() != null) {
                                userMap.put("name", fbUser.getDisplayName());
                                userMap.put("uid", fbUser.getUid());
                            }

                            docRef.update("users", FieldValue.arrayUnion(userMap));

                            user = new User(fbUser.getDisplayName(), fbUser.getUid());

                            leaveJoinButton.setText("Leave");
                            break;
                    }

                }
            });
        }
    }

    private void deleteEvent() {
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");

                startActivity(new Intent(context, FeedActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (fbUser != null && fbUser.getUid().equals(event.getOwner().getUid())) {
            getMenuInflater().inflate(R.menu.action_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_event:
                deleteEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
