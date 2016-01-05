package com.messenger.messengerservers;

public class ConnectionException extends Exception {

    public ConnectionException() {
        super("Action cannot be dane without connection");
    }
}
