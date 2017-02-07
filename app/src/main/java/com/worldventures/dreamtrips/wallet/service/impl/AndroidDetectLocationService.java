package com.worldventures.dreamtrips.wallet.service.impl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.support.v4.content.PermissionChecker;

import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

public class AndroidDetectLocationService implements WalletDetectLocationService {

   private final ReactiveLocationProvider locationProvider;
   private final Context context;

   public AndroidDetectLocationService(Context context) {
      this.locationProvider = new ReactiveLocationProvider(context);
      this.context = context;
   }

   @Override
   public boolean isPermissionGranted() {
      return PermissionChecker.checkSelfPermission(context, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED;
   }

   @Override
   public Observable<Location> detectLastKnownLocation() {
      return locationProvider.getLastKnownLocation();
   }

   @Override
   public Observable<Address> obtainAddressByGeoposition(double latitude, double longitude) {
      return locationProvider.getReverseGeocodeObservable(latitude, longitude, 1).map(addresses -> addresses.get(0));
   }
}
