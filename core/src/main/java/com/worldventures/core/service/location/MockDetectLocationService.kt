package com.worldventures.core.service.location

import android.location.Location
import rx.Observable
import rx.subjects.PublishSubject

class MockDetectLocationService(private val location: Location? = null) : DetectLocationService {

   private val settingsStateSubject = PublishSubject.create<Boolean>()

   override fun isPermissionGranted(): Boolean = true

   override fun isEnabled(): Boolean = true

   override fun fetchLastKnownLocationSettings(): Observable<SettingsResult> = Observable.empty()

   override fun observeLocationSettingState(): Observable<Boolean> = settingsStateSubject

   override fun detectLastKnownLocation(): Observable<Location>
         = if (location == null) Observable.empty() else Observable.just(location)

   fun pushNewLocationSettingsState(enabled: Boolean) {
      settingsStateSubject.onNext(enabled)
   }
}
