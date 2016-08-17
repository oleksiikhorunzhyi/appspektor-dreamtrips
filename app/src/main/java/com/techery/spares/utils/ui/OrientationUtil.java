package com.techery.spares.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Static methods related to device orientation.
 */
public class OrientationUtil {
   private OrientationUtil() {
   }

   /**
    * Locks the device window in landscape mode.
    */
   public static void lockOrientationLandscape(Activity activity) {
      activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
   }

   /**
    * Locks the device window in portrait mode.
    */
   public static void lockOrientationPortrait(Activity activity) {
      activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   }

   /**
    * Locks the device window in actual screen mode.
    */
   public static void lockOrientation(Activity activity) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
         activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
         return;
      }

      final int orientation = activity.getResources().getConfiguration().orientation;
      final int rotation = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
            .getRotation();

      if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
         if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
         }
      } else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_270) {
         if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
         } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
         }
      }
   }

   /**
    * Unlocks the device window in user defined screen mode.
    */
   public static void unlockOrientation(Activity activity) {
      activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
   }

}