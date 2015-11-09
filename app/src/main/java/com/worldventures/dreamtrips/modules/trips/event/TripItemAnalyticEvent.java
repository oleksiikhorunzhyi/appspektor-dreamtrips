package com.worldventures.dreamtrips.modules.trips.event;

public class TripItemAnalyticEvent {

    String tripId;

    String actionAttribute;

    public TripItemAnalyticEvent(String actionAttribute, String tripId) {
        this.tripId = tripId;
        this.actionAttribute = actionAttribute;
    }

    public String getTripId() {
        return tripId;
    }

    public String getActionAttribute() {
        return actionAttribute;
    }
}
