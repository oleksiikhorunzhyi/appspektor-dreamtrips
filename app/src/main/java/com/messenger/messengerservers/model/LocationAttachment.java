package com.messenger.messengerservers.model;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

import timber.log.Timber;

public class LocationAttachment implements Attachment {

    public static String COORDINATE_DIVIDER = ",";

    @SerializedName("ll")
    private String coordinates;

    public double getLat(){
        return parseCoordinate(0);
    }

    public double getLng(){
        return parseCoordinate(1);
    }

    private double parseCoordinate(int coordPosition) {
        return Double.parseDouble(coordinates.split(COORDINATE_DIVIDER)[coordPosition]);
    }

    public LocationAttachment(double lat, double lng) {
        this.coordinates = String.format(Locale.ENGLISH, "%.7f%s%.7f", lat, COORDINATE_DIVIDER, lng);
        Timber.i("Create string " + coordinates + " from " + lat + " and " +lng);
    }

}
