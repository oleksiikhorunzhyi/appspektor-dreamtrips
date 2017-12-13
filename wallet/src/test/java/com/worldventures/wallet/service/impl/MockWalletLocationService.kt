package com.worldventures.wallet.service.impl

import android.location.Location
import com.worldventures.wallet.service.location.SettingsResult
import com.worldventures.wallet.service.location.WalletDetectLocationService
import rx.Observable
import rx.subjects.PublishSubject

class MockWalletLocationService(private val location: Location? = null) : WalletDetectLocationService {

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
