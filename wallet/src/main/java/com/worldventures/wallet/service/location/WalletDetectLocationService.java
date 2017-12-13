package com.worldventures.wallet.service.location;

import android.location.Location;

import rx.Observable;

public interface WalletDetectLocationService {

   boolean isPermissionGranted();

   boolean isEnabled();

   Observable<SettingsResult> fetchLastKnownLocationSettings();

   Observable<Boolean> observeLocationSettingState();

   Observable<Location> detectLastKnownLocation();
}
