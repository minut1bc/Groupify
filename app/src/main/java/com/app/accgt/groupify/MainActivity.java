package com.app.accgt.groupify;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.app.accgt.groupify.models.Event;
import com.app.accgt.groupify.models.Location;
import com.app.accgt.groupify.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final int RC_SIGN_IN = 1;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() != null) {
            // signed in
            String token = FirebaseInstanceId.getInstance().getToken();
            User user = new User(auth.getCurrentUser().getUid());
            db.collection("users").document().set(user);
            Log.d(TAG, "Signed in as " + auth.getCurrentUser().getDisplayName());

        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
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

                    // get the FCM token
                    String token = FirebaseInstanceId.getInstance().getToken();

                    // save the user info in the database to /users/$UID/
                    User user = new User(fbUser.getUid());
                    db.collection("users").document().set(user);
                    Log.d(TAG, "Signed in with UID " + user.getUid());

                } else {
                    // Sign in failed, check response for error code
                    if (response != null)
                        Toasty.error(this, response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
