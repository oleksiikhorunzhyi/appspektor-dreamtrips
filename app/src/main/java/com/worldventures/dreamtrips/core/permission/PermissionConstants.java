package com.worldventures.dreamtrips.core.permission;

import android.Manifest;

public class PermissionConstants {
    public static final String[] STORE_PERMISSIONS = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] LOCATION_PERMISSIONS = new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
}
