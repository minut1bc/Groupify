package com.app.accgt.groupify.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.app.accgt.groupify.R;

public class EventHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView participantsNumber;
    public TextView duration;
    public EventHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.event_name);
        participantsNumber = itemView.findViewById(R.id.event_participants_number);
        duration = itemView.findViewById(R.id.event_duration);
    }
}
