package com.worldventures.dreamtrips.social.ui.infopages.util;

import android.location.Location;

import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.ui.util.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

import static com.worldventures.core.ui.util.permission.PermissionConstants.LOCATION_PERMISSIONS;

public class PermissionLocationDelegate {

   private LocationDelegate locationDelegate;
   private PermissionDispatcher permissionDispatcher;

   private Action1<String[]> needRationalAction;
   private Action1<Location> locationObtainedAction;

   public PermissionLocationDelegate(LocationDelegate locationDelegate, PermissionDispatcher permissionDispatcher) {
      this.locationDelegate = locationDelegate;
      this.permissionDispatcher = permissionDispatcher;
   }

   public void setNeedRationalAction(Action1<String[]> showRationalListener) {
      this.needRationalAction = showRationalListener;
   }

   public void setLocationObtainedAction(Action1<Location> locationObtainedAction) {
      this.locationObtainedAction = locationObtainedAction;
   }

   public void requestPermission(boolean withExplanation, Observable.Transformer viewStopper) {
      permissionDispatcher.requestPermission(LOCATION_PERMISSIONS, withExplanation)
            .compose(bindIoToMain(viewStopper))
            .subscribe(new PermissionSubscriber()
                  .onPermissionDeniedAction(() -> locationObtainedAction.call(null))
                  .onPermissionRationaleAction(() -> onPermissionRational(withExplanation))
                  .onPermissionGrantedAction(() -> onPermissionGranted(viewStopper)));
   }

   public void recheckPermissionAccepted(boolean recheckPermission, Observable.Transformer viewStopper) {
      if (recheckPermission) requestPermission(false, viewStopper);
      else locationObtainedAction.call(null);
   }

   private void onPermissionRational(boolean needShowExplanation) {
      if (needShowExplanation) needRationalAction.call(LOCATION_PERMISSIONS);
      else locationObtainedAction.call(null);
   }

   private void onPermissionGranted(Observable.Transformer stopper) {
      doWithLocation()
            .compose(bindIoToMain(stopper))
            .subscribe(locationObtainedAction::call, e -> locationObtainedAction.call(null));
   }

   // FIXME: 7/5/17 locationDelegate.requestLocationUpdate() holds reference to the context even after unSubscribe
   // Bug is known (https://github.com/mcharmas/Android-ReactiveLocation/pull/142) but the library seems to be abandoned.
   private Observable<Location> doWithLocation() {
      return locationDelegate.requestLocationUpdate()
            .timeout(2, TimeUnit.SECONDS)
            .take(1);
   }

   private <T> Observable.Transformer<T, T> bindIoToMain(Observable.Transformer stopper) {
      return input -> input.compose(new IoToMainComposer<>()).compose(stopper);
   }

}
