package com.worldventures.dreamtrips.core.api.error;

public class DtApiException extends Exception {
    private int httpStatus;
    private ErrorResponse errorResponse;

    public DtApiException(ErrorResponse errorResponse, int httpStatus) {
        this.errorResponse = errorResponse;
        this.httpStatus = httpStatus;
    }

    public DtApiException(String detailMessage) {
        super(detailMessage);
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public int getHttpCode() {
        return httpStatus;
    }
}
