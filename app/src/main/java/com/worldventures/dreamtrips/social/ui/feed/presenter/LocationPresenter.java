package com.worldventures.dreamtrips.social.ui.feed.presenter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.ui.util.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.LocationUtils;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.core.model.Location;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.PostLocationPickerCallback;
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

import static com.worldventures.core.ui.util.permission.PermissionConstants.LOCATION_PERMISSIONS;

public class LocationPresenter<V extends LocationPresenter.View> extends Presenter<LocationPresenter.View> {

   @Inject LocationDelegate gpsLocationDelegate;
   @Inject PostLocationPickerCallback postLocationPickerCallback;
   @Inject PermissionDispatcher permissionDispatcher;

   private boolean isCanceled;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      gpsLocationDelegate.setPermissionView(null);
      checkPermissions(true);

   }

   private void checkPermissions(final boolean withExplanation) {
      permissionDispatcher.requestPermission(LOCATION_PERMISSIONS, withExplanation)
            .compose(bindView())
            .subscribe(new PermissionSubscriber()
                  .onPermissionDeniedAction(this::onPermissionDenied)
                  .onPermissionGrantedAction(this::onPermissionGranted)
                  .onPermissionRationaleAction(() -> view.showPermissionExplanationText(LOCATION_PERMISSIONS)));
   }

   public void recheckPermissionAccepted(boolean accepted) {
      if (accepted) {
         checkPermissions(false);
      } else {
         onPermissionDenied();
      }
   }

   public void onPermissionGranted() {
      gpsLocationDelegate.requestLocationUpdate()
            .compose(bindViewToMainComposer())
            .subscribe(this::onLocationObtained, this::onLocationError);
   }

   public void onPermissionDenied() {
      view.hideProgress();
      view.showPermissionDenied(LOCATION_PERMISSIONS);
      locationNotGranted();
   }

   @Override
   public void dropView() {
      super.dropView();
      gpsLocationDelegate.dropPermissionView();
   }

   public Observable<Location> getLocation() {
      return gpsLocationDelegate.getLastKnownLocation()
            .compose(bindViewToMainComposer())
            .map(this::getLocationFromAndroidLocation);
   }

   public boolean isGpsOn() {
      return LocationUtils.isGpsOn(context);
   }

   public void stopDetectLocation() {
      isCanceled = true;
      view.hideProgress();
   }

   @Nullable
   private Location getLocationFromAndroidLocation(android.location.Location location) {
      view.hideProgress();
      if (isCanceled) {
         return null;
      }

      Geocoder coder = new Geocoder(view.getContext(), Locale.ENGLISH);
      Location newLocation = new Location();
      try {
         List<Address> results = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
         if (!results.isEmpty()) {
            Address address = results.get(0);
            newLocation.setLat(address.getLatitude());
            newLocation.setLng(address.getLongitude());
            newLocation.setName(address.getCountryName() + " " + address.getLocality());
         }
      } catch (IOException e) {
         Timber.e(e, "");
      }
      return newLocation;
   }

   private void onStatusError(Status status) {
      view.resolutionRequired(status);
   }

   private void onLocationError(Throwable e) {
      if (e instanceof LocationDelegate.LocationException) {
         onStatusError(((LocationDelegate.LocationException) e).getStatus());
      } else {
         locationNotGranted();
      }
   }

   private void onLocationObtained(android.location.Location location) {
      gpsLocationDelegate.onLocationObtained(location);
   }

   public void locationNotGranted() {
      gpsLocationDelegate.onLocationObtained(null);
   }

   public void onDone(Location location) {
      if (postLocationPickerCallback != null) {
         postLocationPickerCallback.onLocationPicked(location);
      }
   }

   public interface View extends RxView, PermissionUIComponent {

      void resolutionRequired(Status status);

      Context getContext();

      void showProgress();

      void hideProgress();
   }
}
