package com.messenger.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Convenience class to launch Google Maps intents, read more here:
 * https://developers.google.com/maps/documentation/android-api/intents
 */
public class ExternalMapLauncher {

    private Context context;

    private double latitude;
    private double longitude;
    private int zoomLevel;

    public ExternalMapLauncher(Context context) {
        this.context = context;
    }

    public ExternalMapLauncher setLocationWithMarker(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        return this;
    }

    public ExternalMapLauncher setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
        return this;
    }

    public boolean launch() {
        String locationWithMarkerFormat = "geo:<%1$s>,<%2$s>?q=<%1$s>,<%2$s>";
        String uri = String.format(locationWithMarkerFormat, latitude, longitude);
        if (zoomLevel > 0) {
            uri += String.format("?z=%s", zoomLevel);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        if (context.getPackageManager().resolveActivity(intent, 0) != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }
}
