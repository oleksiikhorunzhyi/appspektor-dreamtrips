package com.techery.spares.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v4.app.Fragment;

import timber.log.Timber;

public class FragmentUtil {

   private FragmentUtil() {
   }

   public static void startSafely(Fragment fragment, Intent intent) {
      try {
         fragment.startActivity(intent);
      } catch (ActivityNotFoundException e) {
         Timber.w(e, "Can't find suitable activity to handle");
      }
   }
}
