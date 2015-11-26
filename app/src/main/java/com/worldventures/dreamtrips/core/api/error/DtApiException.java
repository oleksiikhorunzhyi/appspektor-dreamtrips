package com.worldventures.dreamtrips.core.api.error;

public class DtApiException extends Exception {
    private ErrorResponse errorResponse;

    public DtApiException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public DtApiException(String detailMessage) {
        super(detailMessage);
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
