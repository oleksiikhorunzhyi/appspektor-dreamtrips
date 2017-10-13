package com.worldventures.core.modules.picker.helper;

import com.worldventures.core.ui.util.permission.PermissionConstants;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.ui.util.permission.PermissionSubscriber;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class PickerPermissionChecker {

   public static final String[] PERMISSIONS = PermissionConstants.READ_STORAGE_PERMISSION;

   private PermissionDispatcher permissionDispatcher;

   private Action0 actionRational;
   private Action0 actionDenied;
   private Action0 actionGranted;

   public PickerPermissionChecker(PermissionDispatcher permissionDispatcher) {
      this.permissionDispatcher = permissionDispatcher;
   }

   public void registerCallback (Action0 actionGranted, Action0 actionDenied, Action0 actionRational) {
      this.actionGranted = actionGranted;
      this.actionDenied = actionDenied;
      this.actionRational = actionRational;
   }

   public void checkPermission() {
      checkPermission(actionRational != null);
   }

   public void recheckPermission(boolean needRecheck) {
      if (needRecheck) {
         checkPermission(false);
      } else {
         actionDenied.call();
      }
   }

   private void checkPermission(boolean withExplanation) {
      permissionDispatcher.requestPermission(PickerPermissionChecker.PERMISSIONS, withExplanation)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new PermissionSubscriber()
                  .onPermissionGrantedAction(actionGranted::call)
                  .onPermissionDeniedAction(actionDenied::call)
                  .onPermissionRationaleAction(() -> {
                     if (actionRational != null) actionRational.call();
                  }));
   }

}
