package com.worldventures.core.ui.util.permission;

import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

final class PermissionUtils {

   private PermissionUtils() {
   }

   static boolean verifyPermissions(@Nullable int... grantResults) {
      if (grantResults == null) { return false; }

      for (int result : grantResults) {
         if (result != PackageManager.PERMISSION_GRANTED) {
            return false;
         }
      }
      return true;
   }
}
