package com.worldventures.dreamtrips.util;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;

import java.io.IOException;

import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpServiceException;

import static com.worldventures.dreamtrips.util.ThrowableUtils.getCauseByType;

public class JanetHttpErrorHandlingUtils {
   private JanetHttpErrorHandlingUtils() {

   }

   public static String handleJanetHttpError(Context context, Object action, Throwable exception, String fallbackMessage) {
      if (action instanceof BaseHttpAction && exception instanceof HttpServiceException) {
         ErrorResponse errorResponse = new ErrorResponse();
         com.worldventures.dreamtrips.api.api_common.error.ErrorResponse errorResponseNew = ((BaseHttpAction) action)
               .errorResponse();
         if (errorResponseNew != null) errorResponse.setErrors(errorResponseNew.errors());

         if (getCauseByType(IOException.class, exception.getCause()) != null) {
            return context.getString(R.string.no_connection);
         } else if (errorResponse != null && errorResponse.getErrors() != null && !errorResponse.getErrors()
               .isEmpty()) {
            return errorResponse.getFirstMessage();
         } else return fallbackMessage;
      }

      if (exception instanceof JanetActionException) {
         JanetActionException actionError = (JanetActionException) exception;
         return handleJanetHttpError(context, actionError.getAction(), actionError.getCause(), fallbackMessage);
      }

      if (exception.getCause() != null) {
         return handleJanetHttpError(context, action, exception.getCause(), fallbackMessage);
      }

      return fallbackMessage;
   }
}
