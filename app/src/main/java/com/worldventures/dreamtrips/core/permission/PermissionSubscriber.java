package com.worldventures.dreamtrips.core.permission;

import rx.Subscriber;
import rx.functions.Action0;

public class PermissionSubscriber extends Subscriber<PermissionsResult> {
    private Action0 permissionGrantedAction;
    private Action0 permissionDeniedAction;

    public PermissionSubscriber onPermissionGrandedAction(Action0 permissionGrantedAction) {
        this.permissionGrantedAction = permissionGrantedAction;
        return this;
    }

    public PermissionSubscriber onPermissionDeniedAction(Action0 permissionDeniedAction) {
        this.permissionDeniedAction = permissionDeniedAction;
        return this;
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onNext(PermissionsResult permissionsResult) {
        if (PermissionUtils.verifyPermissions(permissionsResult.grantResults)) {
            if (permissionGrantedAction != null) permissionGrantedAction.call();
        } else {
            if (permissionDeniedAction != null) permissionDeniedAction.call();
        }
    }
}
