package com.app.accgt.groupify.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.accgt.groupify.R;

public class EventHolder extends RecyclerView.ViewHolder {
    public LinearLayout eventItem;
    public TextView name;
    public TextView participantsNumber;
    public TextView duration;
    public TextView location;
    public TextView time;

    public EventHolder(@NonNull View itemView) {
        super(itemView);
        eventItem = itemView.findViewById(R.id.event_item);
        name = itemView.findViewById(R.id.event_name_item);
        participantsNumber = itemView.findViewById(R.id.event_participants_number_item);
        duration = itemView.findViewById(R.id.event_duration_item);
        location = itemView.findViewById(R.id.event_location_item);
        time = itemView.findViewById(R.id.event_time_item);
    }
}
