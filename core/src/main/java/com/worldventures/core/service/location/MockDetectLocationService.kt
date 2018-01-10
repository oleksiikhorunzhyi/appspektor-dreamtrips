package com.worldventures.core.service.location

import android.location.Location
import rx.Observable

class MockDetectLocationService(private val location: Location? = null) : DetectLocationService {

   override fun isPermissionGranted(): Boolean = true

   override fun isEnabled(): Boolean = true

   override fun fetchLastKnownLocationSettings(): Observable<SettingsResult> = Observable.empty()

   override fun observeLocationSettingState(): Observable<Boolean> = Observable.just(true)

   override fun detectLastKnownLocation(): Observable<Location>
         = if (location == null) Observable.empty() else Observable.just(location)
}
