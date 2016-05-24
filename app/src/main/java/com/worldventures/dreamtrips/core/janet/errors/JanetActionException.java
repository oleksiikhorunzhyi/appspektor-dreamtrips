package com.worldventures.dreamtrips.core.janet.errors;

import io.techery.janet.JanetException;

public class JanetActionException extends JanetException {

    private final Object action;

    public JanetActionException(Throwable cause, Object action) {
        super(cause);
        this.action = action;
    }

    public Object getAction() {
        return action;
    }
}
