package com.worldventures.dreamtrips.wallet.service;

import android.location.Address;
import android.location.Location;

import rx.Observable;

public interface WalletDetectLocationService {

   boolean isPermissionGranted();

   Observable<Location> detectLastKnownLocation();

   Observable<Address> obtainAddressByGeoposition(double latitude, double longitude);
}
