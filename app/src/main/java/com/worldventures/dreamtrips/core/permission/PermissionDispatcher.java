package com.worldventures.dreamtrips.core.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.subjects.PublishSubject;

public class PermissionDispatcher {
    private final PublishSubject<PermissionsResult> permissionResultBus = PublishSubject.create();
    private final WeakReference<Activity> activityReference;

    public PermissionDispatcher(Activity activity) {
        this.activityReference = new WeakReference<>(activity);
    }

    public Observable<PermissionsResult> requestPermissionFor(String[] permissions) {
        Activity activity = activityReference.get();
        if (activity == null) return Observable.empty();

        // Use 2 lower bytes, this ensures the uniqueness.
        int requestCode = (char) System.currentTimeMillis();
        // FragmentActivity#validateRequestPermissionsRequestCode throws exception:
        // "Can only use lower 16 bits for requestCode"

        if (hasSelfPermissions(permissions)) {
            return Observable.just(createSuccessResult(permissions, requestCode));
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return permissionResultBus
                    .filter(result -> result.requestCode == requestCode)
                    .take(1);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionResultBus.onNext(new PermissionsResult(requestCode, permissions, grantResults));
    }

    private boolean hasSelfPermissions(String[] permissions) {
        Activity activity = activityReference.get();
        if (activity == null) return false;

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private PermissionsResult createSuccessResult(String[] permissions, int requestCode) {
        int[] grantResults = new int[permissions.length];
        for (int i = 0; i < grantResults.length; i++) grantResults[i] = PackageManager.PERMISSION_GRANTED;
        return new PermissionsResult(requestCode, permissions, grantResults);
    }
}
