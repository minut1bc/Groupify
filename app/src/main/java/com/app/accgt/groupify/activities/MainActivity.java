package com.app.accgt.groupify.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.app.accgt.groupify.R;
import com.app.accgt.groupify.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final int RC_SIGN_IN = 1;

    private Context context;
    private FirebaseFirestore db;
    private Intent feedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        feedIntent = new Intent(context, FeedActivity.class);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() != null) {
            // signed in
            User user = new User(auth.getCurrentUser().getUid(), auth.getCurrentUser().getDisplayName());
            db.collection("users").document().set(user);
            Log.d(TAG, "Signed in as " + auth.getCurrentUser().getDisplayName());
            startActivity(feedIntent);
        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(true)
                            .setLogo(R.mipmap.ic_launcher_groupify)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN:
                IdpResponse response = IdpResponse.fromResultIntent(data);

                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    // get the current user
                    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (fbUser != null) {
                        // save the user info in the database to /users/$UID/
                        User user = new User(fbUser.getUid(), fbUser.getDisplayName());
                        db.collection("users").document().set(user);
                        Log.d(TAG, "Signed in with UID " + user.getUid());

                        startActivity(feedIntent);
                    }
                } else {
                    // Sign in failed, check response for error code
                    if (response != null && response.getError() != null) {
                        Toasty.error(context, response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
