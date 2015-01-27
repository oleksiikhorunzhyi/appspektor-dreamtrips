package com.worldventures.dreamtrips.core.model.response;

import com.worldventures.dreamtrips.core.model.DTServerError;

public class ErrorResponse {
    DTServerError errors;

    public DTServerError getErrors() {
        return errors;
    }
}
