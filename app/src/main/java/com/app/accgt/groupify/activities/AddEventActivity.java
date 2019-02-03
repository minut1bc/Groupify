package com.app.accgt.groupify.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.accgt.groupify.R;
import com.app.accgt.groupify.models.Event;
import com.app.accgt.groupify.models.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddEventActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = FeedActivity.class.getSimpleName();

    private static final int PLACE_PICKER_REQUEST = 1;

    private EditText addEventName;
    private EditText addEventDescription;
    private TextView addEventLocation;
    private TextView addEventTime;
    private EditText addEventDuration;
    private Calendar date;
    private Context context;
    private Event event = new Event();
    private Button createEvent;
    private GoogleApiClient googleApiClient;
    private Location location = new Location();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        context = this;

        googleApiClient = new GoogleApiClient
                .Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this).build();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        addEventName = findViewById(R.id.event_name_edit);
        addEventDescription = findViewById(R.id.event_description_edit);
        addEventLocation = findViewById(R.id.event_location_edit);
        addEventTime = findViewById(R.id.event_time_edit);
        addEventDuration = findViewById(R.id.event_duration_edit);
        createEvent = findViewById(R.id.create_event_button);

        addEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        addEventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(AddEventActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allFieldsValid()) {
                    event.setName(addEventName.getText().toString());
                    event.setDescription(addEventDescription.getText().toString());
                    event.setDuration(Integer.parseInt(addEventDuration.getText().toString()));
                    event.setTimestamp(new GregorianCalendar().getTime());
                    event.setLocation(location);
                    event.setUsers(new ArrayList<FirebaseUser>());
                    Log.d(TAG, "Event created");
                    FirebaseFirestore.getInstance().collection("events").document().set(event);
                    finish();
                }
            }
        });
    }

    private boolean allFieldsValid() {
        boolean valid = true;
        String toastMessage = "";
        if (addEventName.getText().toString().equals("")) {
            toastMessage = "Please insert an event name";
            valid = false;
        } else if (addEventDescription.getText().toString().equals("")) {
            toastMessage = "Please insert an event description";
            valid = false;
        } else if (addEventLocation.getText().toString().equals("my location")) {
            toastMessage = "Please pick an event location";
            valid = false;
        } else if (addEventTime.getText().toString().equals("my time")) {
            toastMessage = "Please pick an event time";
            valid = false;
        } else if (addEventDuration.getText().toString().equals("")) {
            toastMessage = "Please insert a numeric integer value for the duration";
            valid = false;
        } else if (!addEventDuration.getText().toString().equals("")) {
            try {
                Integer.parseInt(addEventDuration.getText().toString());
            } catch (NumberFormatException exception) {
                toastMessage = "Please insert a numeric integer value for the duration";
                valid = false;
            }
        }

        if (!valid) {
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        }

        return valid;
    }

    public void showDateTimePicker() {
        date = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        addEventTime.setText(date.getTime().toString());
                        event.setTime(date.getTime());
                    }
                }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false).show();
            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(createEvent, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                StringBuilder stBuilder = new StringBuilder();
                String placeName = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ").append(placeName).append("\n")
                        .append("Latitude: ").append(latitude).append("\n")
                        .append("Longitude: ").append(longitude).append("\n")
                        .append("Address: ").append(address);
                addEventLocation.setText(stBuilder.toString());
                location = new Location(placeName, Double.valueOf(latitude), Double.valueOf(longitude));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }
}
