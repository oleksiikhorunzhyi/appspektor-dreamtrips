package com.worldventures.core.service.location;

import android.location.Location;

import rx.Observable;

public interface DetectLocationService {

   boolean isPermissionGranted();

   boolean isEnabled();

   Observable<SettingsResult> fetchLastKnownLocationSettings();

   Observable<Boolean> observeLocationSettingState();

   Observable<Location> detectLastKnownLocation();
}
