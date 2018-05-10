package com.worldventures.core.ui.util.permission;

import android.content.pm.PackageManager;
import android.text.TextUtils;

import org.jetbrains.annotations.Nullable;

public final class PermissionUtils {

   public boolean equals(@Nullable String[] group1, @Nullable String[] group2) {
      if (group1 == null && group2 == null) {
         return true;
      }
      if (group1 == null || group2 == null) {
         return false;
      }
      if (group1.length != group2.length) {
         return false;
      }

      for (String permissionFromGroup1 : group1) {
         boolean foundPermission = false;

         for (String permissionFromGroup2 : group2) {
            if (TextUtils.equals(permissionFromGroup1, permissionFromGroup2)) {
               foundPermission = true;
               break;
            }
         }

         if (!foundPermission) {
            return false;
         }
      }

      return true;
   }

   public static boolean verifyPermissions(@Nullable int[] grantResults) {
      if (grantResults == null || grantResults.length == 0) {
         return false;
      }
      for (int result : grantResults) {
         if (result != PackageManager.PERMISSION_GRANTED) {
            return false;
         }
      }
      return true;
   }
}
