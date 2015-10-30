package com.worldventures.dreamtrips.modules.dtl.event;


import android.location.Location;

public class LocationObtainedEvent {

    private Location location;

    public LocationObtainedEvent() {
    }

    public LocationObtainedEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
