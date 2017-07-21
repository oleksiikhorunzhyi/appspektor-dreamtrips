package com.worldventures.dreamtrips.wallet.service.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.PermissionChecker;

import com.google.android.gms.location.LocationSettingsResult;

import java.util.concurrent.TimeUnit;

import rx.Observable;

import static rx.Observable.create;

public class AndroidDetectLocationService implements WalletDetectLocationService {

   private final Context context;
   private final LocationManager locationManager;

   public AndroidDetectLocationService(Context context) {
      this.context = context;
      this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
   }

   @Override
   public boolean isPermissionGranted() {
      return PermissionChecker.checkSelfPermission(context, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED;
   }

   @Override
   public boolean isEnabled() {
      return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
   }

   @Override
   public Observable<LocationSettingsResult> fetchLastKnownLocationSettings() {
      return CheckLocationSettings.createObservable(context, LastKnownLocationObservable.provideLocationRequest());
   }

   @Override
   public Observable<Boolean> observeLocationSettingState() {
      final RxLocationAdapter adapter = new RxLocationAdapter(context);
      return Observable.create(adapter)
            .map(aVoid -> isEnabled())
            .distinctUntilChanged()
            .doOnUnsubscribe(adapter::release);
   }

   @Override
   public Observable<Location> detectLastKnownLocation() {
      return create(new LastKnownLocationObservable(context)).timeout(6, TimeUnit.SECONDS);
   }
}
