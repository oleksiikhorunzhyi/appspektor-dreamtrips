package com.worldventures.dreamtrips.core.permission;

public class PermissionsResult {
    public final int requestCode;
    public final String[] permissions;
    public final int[] grantResults;

    public PermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.grantResults = grantResults;
    }
}