package com.teal.a276.walkinggroup.model.dataobjects;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UserLocation {
    private Double lat;
    private Double lng;
    private String timestamp;

    public UserLocation() {}

    public UserLocation(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();

        // Inspired by: https://stackoverflow.com/questions/3914404/how-to-get-current-moment-in-iso-8601-format-with-date-hour-and-minute
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String time = df.format(new Date());
        Log.i("Time", time);
        this.timestamp = time;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timeStamp) {
        this.timestamp = timeStamp;
    }
}
