package com.app.accgt.groupify.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.app.accgt.groupify.R;
import com.app.accgt.groupify.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class EventActivity extends AppCompatActivity {
    private static final String TAG = EventActivity.class.getSimpleName();

    private Event event;

    private TextView eventName;
    private TextView eventDescription;
    private TextView eventDuration;
    private TextView eventLocation;
    private TextView eventTime;
    private TextView eventTimeStamp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventName = findViewById(R.id.event_name);
        eventDescription = findViewById(R.id.event_description);
        eventDuration = findViewById(R.id.event_duration);
        eventLocation = findViewById(R.id.event_location);
        eventTime = findViewById(R.id.event_time);
        eventTimeStamp = findViewById(R.id.event_time_stamp);

        final String eventId = getIntent().getStringExtra("eventId");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("events").document(eventId);
        Source source = Source.CACHE;
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    event = document.toObject(Event.class);
                    populateEvent();
                    Log.d(TAG, "Cached document data " + document.getData());
                } else {
                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });
    }

    private void populateEvent() {
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());
        eventDuration.setText(String.valueOf(event.getDuration()));
        eventLocation.setText(event.getLocation().getName());
        eventTime.setText(event.getTime().toString());
        eventTimeStamp.setText(event.getTimestamp().toString());
    }
}
