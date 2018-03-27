package com.teal.a276.walkinggroup.model.dataobjects;

import android.location.Location;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Matt on 2018-03-26.
 */

public class UserLocation {
    private Double lat;
    private Double lng;
    private String timestamp;

    public UserLocation() {}
    public UserLocation(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();

        // TODO: fix this hardcoded string
        this.timestamp = "2012-04-23T18:25:43.511Z";
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
