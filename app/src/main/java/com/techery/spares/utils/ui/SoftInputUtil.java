package com.techery.spares.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftInputUtil {
   private SoftInputUtil() { }

   /** Show soft keyboard explicitly */
   public static void showSoftInputMethod(View view) {
      if (view == null) return;
      //
      view.requestFocus();
      InputMethodManager inputManager = (InputMethodManager) view.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
      inputManager.showSoftInput(view, 0);
   }

   /** Show soft keyboard explicitly */
   public static void showSoftInputMethod(Context context) {
      InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
      inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
   }

   /** Hide soft keyboard if visible */
   public static void hideSoftInputMethod(Activity activity) {
      try {
         InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
         View currentFocus = activity.getWindow().getCurrentFocus();
         if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            currentFocus.clearFocus();
         } else {
            inputManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
         }
      } catch (Exception e) {
         // current focus could be out of visibility
      }
   }

   /** Hide soft keyboard if visible */
   public static void hideSoftInputMethod(View view) {
      if (view == null) return;
      //
      InputMethodManager inputManager = (InputMethodManager) view.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
      view.clearFocus();
   }
}
