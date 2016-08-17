package com.worldventures.dreamtrips.modules.feed.presenter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.LocationUtils;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.PermissionView;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.PostLocationPickerCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class LocationPresenter<V extends LocationPresenter.View> extends Presenter<LocationPresenter.View> {

   @Inject LocationDelegate gpsLocationDelegate;
   @Inject PostLocationPickerCallback postLocationPickerCallback;
   private boolean isCanceled;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      gpsLocationDelegate.setPermissionView(view);
      view.checkPermissions();
   }

   public Observable<Location> getLocation() {
      return view.bind(gpsLocationDelegate.getLastKnownLocation())
            .compose(new IoToMainComposer<>())
            .map(this::getLocationFromAndroidLocation);
   }

   public boolean isGpsOn() {
      return LocationUtils.isGpsOn(context);
   }

   public void stopDetectLocation() {
      isCanceled = true;
      view.hideProgress();
   }

   @NonNull
   private Location getLocationFromAndroidLocation(android.location.Location location) {
      view.hideProgress();
      if (isCanceled) return null;
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

   public void onPermissionGranted() {
      view.bind(gpsLocationDelegate.requestLocationUpdate().compose(new IoToMainComposer<>()))
            .subscribe(this::onLocationObtained, this::onLocationError);
   }

   private void onStatusError(Status status) {
      view.resolutionRequired(status);
   }

   private void onLocationError(Throwable e) {
      if (e instanceof LocationDelegate.LocationException)
         onStatusError(((LocationDelegate.LocationException) e).getStatus());
      else locationNotGranted();
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

   public interface View extends RxView, PermissionView {

      void resolutionRequired(Status status);

      Context getContext();

      void showProgress();

      void hideProgress();
   }
}
