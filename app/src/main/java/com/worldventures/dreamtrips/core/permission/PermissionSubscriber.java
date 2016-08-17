package com.worldventures.dreamtrips.core.permission;

import rx.Subscriber;
import rx.functions.Action0;

public class PermissionSubscriber extends Subscriber<PermissionsResult> {
   private Action0 permissionGrantedAction;
   private Action0 permissionDeniedAction;
   private Action0 permissionRationaleAction;

   public PermissionSubscriber onPermissionGrantedAction(Action0 permissionGrantedAction) {
      this.permissionGrantedAction = permissionGrantedAction;
      return this;
   }

   public PermissionSubscriber onPermissionDeniedAction(Action0 permissionDeniedAction) {
      this.permissionDeniedAction = permissionDeniedAction;
      return this;
   }

   public PermissionSubscriber onPermissionRationaleAction(Action0 permissionRationaleAction) {
      this.permissionRationaleAction = permissionRationaleAction;
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
      if (permissionsResult.shouldShowRequestPermissionRationale) {
         if (permissionRationaleAction != null) permissionRationaleAction.call();
      } else if (PermissionUtils.verifyPermissions(permissionsResult.grantResults)) {
         if (permissionGrantedAction != null) permissionGrantedAction.call();
      } else {
         if (permissionDeniedAction != null) permissionDeniedAction.call();
      }
   }
}
