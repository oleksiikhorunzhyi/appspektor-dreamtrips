package com.worldventures.dreamtrips.core.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.worldventures.dreamtrips.util.SdkVersionUtils;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.subjects.PublishSubject;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class PermissionDispatcher {
    private final PublishSubject<PermissionsResult> permissionResultBus = PublishSubject.create();
    private final WeakReference<Activity> activityReference;

    public PermissionDispatcher(Activity activity) {
        this.activityReference = new WeakReference<>(activity);
    }

    /**
     * Create request permission observable
     * {@link Activity#shouldShowRequestPermissionRationale} will be checked
     * @param permissions list required permissions
     * @return request permission observable
     */
    public Observable<PermissionsResult> requestPermission(String[] permissions) {
        return requestPermission(permissions, true);
    }

    /**
     * Create request permission observable
     * @param permissions list required permissions
     * @param shouldShowRequestRationale need to check {@link Activity#shouldShowRequestPermissionRationale}
     * @return request permission observable
     */
    public Observable<PermissionsResult> requestPermission(String[] permissions, boolean shouldShowRequestRationale) {
        Activity activity = activityReference.get();
        if (activity == null) return Observable.empty();

        // Use 2 lower bytes, this ensures the uniqueness.
        int requestCode = (char) System.currentTimeMillis();
        // FragmentActivity#validateRequestPermissionsRequestCode throws exception:
        // "Can only use lower 16 bits for requestCode"

        if (hasSelfPermissions(activity, permissions)) {
            return Observable.just(createSuccessResult(permissions, requestCode));
        } else {
            return doesNotHaveSelfPermissions(activity, permissions, requestCode, shouldShowRequestRationale);
        }
    }

    private Observable<PermissionsResult> doesNotHaveSelfPermissions(Activity activity, String[] permissions,
                                                                     int requestCode,
                                                                     boolean shouldShowRequestRationale) {
        if (shouldShowRequestRationale && shouldShowRequestPermissionRationale(activity, permissions)) {
            return Observable.just(createPermissionRationaleResult(permissions, requestCode));
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return permissionResultBus
                    .filter(result -> result.requestCode == requestCode)
                    .take(1);
        }
    }

    /**
     * Should be called in {@link Activity#onRequestPermissionsResult}
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Activity activity = activityReference.get();
        if (SdkVersionUtils.getTargetSdkVersion(activity) < Build.VERSION_CODES.M
                && !hasSelfPermissions(activity, permissions)) {
            permissionResultBus.onNext(createDenyResult(permissions, requestCode));
            return;
        }
        permissionResultBus.onNext(new PermissionsResult(requestCode, permissions, grantResults));
    }

    private boolean hasSelfPermissions(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (!hasSelfPermission(activity, permission)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasSelfPermission(Context context, String permission) {
        try {
            return checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException t) {
            return false;
        }
    }

    private boolean shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    private PermissionsResult createSuccessResult(String[] permissions, int requestCode) {
        int[] grantResults = new int[permissions.length];
        for (int i = 0; i < grantResults.length; i++) grantResults[i] = PackageManager.PERMISSION_GRANTED;
        return new PermissionsResult(requestCode, permissions, grantResults);
    }

    private PermissionsResult createDenyResult(String[] permissions, int requestCode) {
        int[] grantResults = new int[permissions.length];
        for (int i = 0; i < grantResults.length; i++) grantResults[i] = PackageManager.PERMISSION_DENIED;
        return new PermissionsResult(requestCode, permissions, grantResults);
    }

    private PermissionsResult createPermissionRationaleResult(String[] permissions, int requestCode) {
        return new PermissionsResult(requestCode, permissions, true);
    }

}
