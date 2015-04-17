package com.worldventures.dreamtrips.core.utils.events;

public class ServerDownEvent {

    private String message;

    public ServerDownEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
