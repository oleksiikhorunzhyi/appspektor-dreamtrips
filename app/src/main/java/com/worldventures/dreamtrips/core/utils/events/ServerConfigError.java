package com.worldventures.dreamtrips.core.utils.events;

public class ServerConfigError {
    String error;

    public ServerConfigError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}