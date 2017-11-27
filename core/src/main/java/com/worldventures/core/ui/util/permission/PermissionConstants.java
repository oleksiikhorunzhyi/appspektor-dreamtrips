package com.worldventures.core.ui.util.permission;

import android.Manifest;

public class PermissionConstants {
   public static final String[] LOCATION_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

   public static final String[] CAMERA_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

   public static final String[] READ_STORAGE_PERMISSION = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
   public static final String[] READ_PHONE_CONTACTS = new String[]{Manifest.permission.READ_CONTACTS};

   public static final String[] WRITE_PHONE_CONTACTS = new String[]{Manifest.permission.WRITE_CONTACTS};
   public static final String[] WRITE_EXTERNAL_STORAGE = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

}
