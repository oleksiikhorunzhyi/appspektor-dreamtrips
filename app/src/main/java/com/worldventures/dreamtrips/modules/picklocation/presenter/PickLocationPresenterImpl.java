package com.worldventures.dreamtrips.modules.picklocation.presenter;

import android.content.Context;
import android.location.Location;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.picklocation.util.LocationPermissionHelper;
import com.worldventures.dreamtrips.modules.picklocation.util.LocationResultHandler;
import com.worldventures.dreamtrips.modules.picklocation.util.LocationSettingsDelegate;
import com.worldventures.dreamtrips.modules.picklocation.view.PickLocationView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class PickLocationPresenterImpl extends MvpBasePresenter<PickLocationView> implements PickLocationPresenter {

   private static final int TIMEOUT_INITIAL_LOCATION_SEC = 20;
   private static final int TIMEOUT_PICK_CURRENT_LOCATION_SEC = 3;

   private static final int MAP_ANIMATION_THRESHOLD_MS = 300;

   private Context context;

   @Inject LocationDelegate locationDelegate;
   @Inject LocationSettingsDelegate locationSettingsDelegate;
   @Inject LocationResultHandler locationResultHandler;
   LocationPermissionHelper permissionHelper;

   public PickLocationPresenterImpl(Context context, LocationPermissionHelper permissionHelper) {
      this.context = context;
      this.permissionHelper = permissionHelper;
      ((Injector) context).inject(this);
   }

   @Override
   public void attachView(PickLocationView view) {
      super.attachView(view);
      locationSettingsDelegate.getLocationSettingsStateObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(locationSettingsEnabled -> {
               if (locationSettingsEnabled) {
                  getView().initMap();
               }
            });
   }

   @Override
   public void onShouldInitMap() {
      if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS) {
         getView().showPlayServicesAbsentOverlay();
      } else {
         permissionHelper.askForLocationPermission();
      }
   }

   @Override
   public void onRationalForLocationPermissionRequired() {
      getView().showRationalForLocationPermission();
   }

   @Override
   public void onLocationPermissionGranted() {
      getView().initMap();
   }

   @Override
   public void onLocationPermissionDenied() {
      getView().showDeniedLocationPermissionError();
   }

   @Override
   public void onMapInitialized(GoogleMap googleMap) {
      if (!getView().isCurrentLocationSet()) {
         updateMapCurrentLocation();
      }
   }

   private void updateMapCurrentLocation() {
      // TODO Make it in more Rx way
      long locationUpdateStartTime = System.currentTimeMillis();
      getLastKnownLocationObservable().timeout(TIMEOUT_INITIAL_LOCATION_SEC, TimeUnit.SECONDS)
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(location -> {
               long timeElapsedToGetLocation = System.currentTimeMillis() - locationUpdateStartTime;
               boolean animate = timeElapsedToGetLocation > MAP_ANIMATION_THRESHOLD_MS;
               getView().setCurrentLocation(location, animate);
            }, this::onLocationError);
   }

   private void onLocationError(Throwable e) {
      if (e instanceof LocationDelegate.LocationException) {
         Status status = (((LocationDelegate.LocationException) e).getStatus());
         locationSettingsDelegate.startLocationSettingsResolution(status);
      } else {
         getView().showObtainLocationError();
         Timber.e(e, "Error getting location");
      }
   }

   @Override
   public int getToolbarMenuRes() {
      return R.menu.menu_pick_location;
   }

   @Override
   public boolean onMenuItemClick(MenuItem item) {
      if (item.getItemId() != R.id.action_done) {
         return false;
      }
      reportLocationPicked();
      return true;
   }

   private void reportLocationPicked() {
      getLastKnownLocationObservable().timeout(TIMEOUT_PICK_CURRENT_LOCATION_SEC, TimeUnit.SECONDS)
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(location -> locationResultHandler.reportResultAndFinish(location, null), e -> {
               Timber.e(e, "Could not get last location");
               getView().showObtainLocationError();
            });
   }

   private Observable<Location> getLastKnownLocationObservable() {
      return locationDelegate.getLastKnownLocation()
            .compose(new IoToMainComposer<>())
            .compose(RxLifecycle.bindView((View) getView()));
   }
}
