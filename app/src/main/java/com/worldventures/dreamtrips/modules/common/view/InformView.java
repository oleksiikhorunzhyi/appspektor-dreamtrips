package com.worldventures.dreamtrips.modules.common.view;

import android.support.annotation.StringRes;

public interface InformView {

   /**
    * Show user a short textual message, e.g. a {@link android.support.design.widget.Snackbar Snackbar}
    *
    * @param message string resource id to show
    */
   void informUser(@StringRes int message);

   /**
    * Show user a short textual message, e.g. a {@link android.support.design.widget.Snackbar Snackbar}
    *
    * @param message message to show
    */
   void informUser(String message);
}
