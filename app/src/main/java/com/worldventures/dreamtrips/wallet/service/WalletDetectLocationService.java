package com.worldventures.dreamtrips.wallet.service;

import android.location.Address;
import android.location.Location;

import rx.Observable;

public interface WalletDetectLocationService {

   boolean isPermissionGranted();

   boolean isEnabled();

   Observable<Boolean> observeLocationSettingState();

   Observable<Location> detectLastKnownLocation();

   Observable<Address> obtainAddressByGeoposition(double latitude, double longitude);
}
