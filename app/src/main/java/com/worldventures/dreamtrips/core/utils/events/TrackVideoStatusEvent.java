package com.worldventures.dreamtrips.core.utils.events;

public class TrackVideoStatusEvent {

    private String action;
    private String name;

    public TrackVideoStatusEvent(String action, String name) {
        this.action = action;
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

}
