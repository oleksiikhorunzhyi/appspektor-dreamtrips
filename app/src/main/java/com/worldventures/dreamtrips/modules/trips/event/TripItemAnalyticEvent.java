package com.worldventures.dreamtrips.modules.trips.event;

public class TripItemAnalyticEvent {

    String tripId;

    String actionAttribute;

    String tripName;

    public TripItemAnalyticEvent(String actionAttribute, String tripId, String tripName) {
        this.tripId = tripId;
        this.actionAttribute = actionAttribute;
        this.tripName = tripName;
    }

    public String getTripId() {
        return tripId;
    }

    public String getActionAttribute() {
        return actionAttribute;
    }

    public String getTripName() {
        return tripName;
    }
}
