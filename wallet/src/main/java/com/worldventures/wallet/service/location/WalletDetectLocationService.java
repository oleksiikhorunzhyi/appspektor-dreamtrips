package com.worldventures.wallet.service.location;

import android.location.Location;

import com.google.android.gms.location.LocationSettingsResult;

import rx.Observable;

public interface WalletDetectLocationService {

   boolean isPermissionGranted();

   boolean isEnabled();

   Observable<LocationSettingsResult> fetchLastKnownLocationSettings();

   Observable<Boolean> observeLocationSettingState();

   Observable<Location> detectLastKnownLocation();
}
