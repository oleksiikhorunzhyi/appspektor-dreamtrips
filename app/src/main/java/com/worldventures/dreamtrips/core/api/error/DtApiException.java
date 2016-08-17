package com.worldventures.dreamtrips.core.api.error;

import retrofit.RetrofitError;

public class DtApiException extends Exception {
   private int httpStatus;
   private ErrorResponse errorResponse;

   public DtApiException(String detailedMessage, ErrorResponse errorResponse, int httpStatus) {
      super(detailedMessage);
      this.errorResponse = errorResponse;
      this.httpStatus = httpStatus;
   }

   public DtApiException(ErrorResponse errorResponse, int httpStatus, RetrofitError retrofitError) {
      super(retrofitError);
      this.errorResponse = errorResponse;
      this.httpStatus = httpStatus;
   }

   public DtApiException(String detailMessage, RetrofitError retrofitError) {
      super(detailMessage, retrofitError);
   }

   public ErrorResponse getErrorResponse() {
      return errorResponse;
   }

   public int getHttpCode() {
      return httpStatus;
   }
}
