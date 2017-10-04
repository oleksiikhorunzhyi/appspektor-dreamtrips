package com.worldventures.core.ui.util.permission;

import rx.Observable;

public class PermissionGrantedComposer implements Observable.Transformer<PermissionsResult, Void> {
   @Override
   public Observable<Void> call(Observable<PermissionsResult> permissionsResultObservable) {
      return permissionsResultObservable.flatMap(this::handlePermissionResult);
   }

   private Observable<Void> handlePermissionResult(PermissionsResult result) {
      if (PermissionUtils.verifyPermissions(result.grantResults)) {
         return Observable.just(null);
      } else {
         return Observable.empty();
      }
   }
}
