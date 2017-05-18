package com.worldventures.dreamtrips.modules.common.view;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.core.api.error.ErrorResponse;

public interface ApiErrorView {

   /**
    * Called in actual view implementation when api error occurs
    *
    * @param errorResponse list of which fields was failed {@link ErrorResponse}
    * @return {@code true} - if error was handled, {@code false} - if should be handled upper.
    */
   boolean onApiError(ErrorResponse errorResponse);

   /**
    * Called if api call failed for some reason, here you can close progress dialog etc.
    */
   void onApiCallFailed();

   /**
    * Show user a short textual message, e.g. a {@link android.support.design.widget.Snackbar Snackbar}
    *
    * @param stringId string resource id to show
    */
   void informUser(@StringRes int stringId);

   /**
    * Show user a short textual message, e.g. a {@link android.support.design.widget.Snackbar Snackbar}
    *
    * @param message to show
    */
   void informUser(String message);

   /**
    * Should return if device is connected to internent
    * @return connection status
    */
   boolean isConnected();
}
