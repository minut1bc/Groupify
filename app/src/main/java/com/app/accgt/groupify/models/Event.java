package com.app.accgt.groupify.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Event {
    private String name;
    private String description;
    private Location location;
    private Date timestamp;
    private Date time;
    private int duration;
    private List<FirebaseUser> users;

    public Event() {
    }

    public Event(String name, String description, Location location, Date time, int duration, List<FirebaseUser> users) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.time = time;
        this.duration = duration;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @ServerTimestamp
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<FirebaseUser> getUsers() {
        return users;
    }

    public void setUsers(List<FirebaseUser> users) {
        this.users = users;
    }
}
