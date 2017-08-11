package com.worldventures.dreamtrips.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.mobilesdk.DreamTripsErrorParser;
import com.worldventures.dreamtrips.mobilesdk.service.ServiceLabel;

import java.io.IOException;

import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpException;
import io.techery.janet.http.exception.HttpServiceException;

import static com.worldventures.dreamtrips.util.ThrowableUtils.getCauseByType;

public class HttpErrorHandlingUtil {

   private final Context context;
   private final DreamTripsErrorParser errorParser;

   public HttpErrorHandlingUtil(Context context, DreamTripsErrorParser errorParser) {
      this.context = context;
      this.errorParser = errorParser;
   }

   public String handleJanetHttpError(Object action, Throwable exception, String fallbackMessage) {
      if (exception instanceof JanetActionException) {
         JanetActionException actionError = (JanetActionException) exception;
         return handleJanetHttpError(actionError.getAction(), actionError.getCause(), fallbackMessage);
      }
      if (isNoInternetConnectionError(exception)) {
         return this.context.getString(R.string.no_connection);
      }
      if (action instanceof ServiceLabel && exception instanceof HttpServiceException) {
         String errorReason = errorParser.parseReason((ServiceLabel) action);
         if (TextUtils.isEmpty(errorReason)) return fallbackMessage;
         else return errorReason;
      }
      if (exception.getCause() != null) {
         return handleJanetHttpError(action, exception.getCause(), fallbackMessage);
      }
      return fallbackMessage;
   }

   public static Pair<BaseHttpAction, Throwable> obtainHttpException(Object action, Throwable exception) {
      if (action instanceof BaseHttpAction && exception instanceof HttpServiceException) {
         return new Pair<>((BaseHttpAction) action, exception);
      }

      if (exception instanceof JanetActionException) {
         JanetActionException actionError = (JanetActionException) exception;
         return obtainHttpException(actionError.getAction(), actionError.getCause());
      }

      if (exception.getCause() != null) {
         return obtainHttpException(action, exception.getCause());
      }

      return null;
   }

   public static boolean isNoInternetConnectionError(Throwable exception) {
      return getCauseByType(IOException.class, exception.getCause()) != null;
   }

   public static String obtainHttpErrorMessage(Throwable exception) {
      if (!isHttpExceptionWithPath(exception)) return null;

      HttpException httpException = (HttpException) exception.getCause();
      return httpException.getCause() != null ? httpException.getCause().getMessage()
            : ((HttpException) exception.getCause()).getResponse().getBody().toString();
   }

   public static String obtainHttpErrorPath(Throwable exception) {
      if (!isHttpExceptionWithPath(exception)) return null;

      String url = ((HttpException) exception.getCause()).getRequest().getUrl();
      return Uri.parse(url).getPath();
   }

   private static boolean isHttpExceptionWithPath(Throwable exception) {
      return exception != null && exception.getCause() != null
            && (exception.getCause() instanceof HttpException)
            && ((HttpException)exception.getCause()).getRequest() == null;
   }
}
