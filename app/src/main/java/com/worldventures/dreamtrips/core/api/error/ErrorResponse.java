package com.worldventures.dreamtrips.core.api.error;

import com.innahema.collections.query.queriables.Queryable;

import java.util.HashMap;

public class ErrorResponse {

    HashMap<String, String[]> errors;

    public HashMap<String, String[]> getErrors() {
        return errors;
    }

    public String getFirstMessage() {
        return Queryable.from(errors.values()).first()[0];
    }

    public String getFirstKey() {
        return Queryable.from(errors.keySet()).first();
    }
}
