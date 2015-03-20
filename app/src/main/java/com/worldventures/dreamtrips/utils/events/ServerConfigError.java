package com.worldventures.dreamtrips.utils.events;

public class ServerConfigError {
    String error;

    public ServerConfigError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
