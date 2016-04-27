package com.worldventures.dreamtrips.core.permission;

import android.content.pm.PackageManager;

public class PermissionUtils {
    public static boolean verifyPermissions(int... grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
