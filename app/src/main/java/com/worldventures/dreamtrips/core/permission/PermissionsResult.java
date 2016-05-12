package com.worldventures.dreamtrips.core.permission;

import android.support.annotation.Nullable;

public class PermissionsResult {
    public final int requestCode;
    @Nullable
    public final String[] permissions;
    public final int[] grantResults;
    public final boolean shouldShowRequestPermissionRationale;

    public PermissionsResult(int requestCode, @Nullable String[] permissions, int[] grantResults) {
        this(requestCode, permissions, grantResults, false);
    }

    public PermissionsResult(int requestCode, @Nullable String[] permissions,
                             boolean shouldShowRequestPermissionRationale) {
        this(requestCode, permissions, null, shouldShowRequestPermissionRationale);
    }

    private PermissionsResult(int requestCode, @Nullable String[] permissions, int[] grantResults,
                              boolean shouldShowRequestPermissionRationale) {
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale;
        this.grantResults = grantResults;
    }
}