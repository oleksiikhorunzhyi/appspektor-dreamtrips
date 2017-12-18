package com.worldventures.wallet.service.impl

import android.location.Location
import com.google.android.gms.location.LocationSettingsResult
import com.worldventures.wallet.service.location.WalletDetectLocationService
import rx.Observable

class MockWalletLocationService(private val location: Location? = null) : WalletDetectLocationService {

   override fun isPermissionGranted(): Boolean = true

   override fun isEnabled(): Boolean = true

   override fun fetchLastKnownLocationSettings(): Observable<LocationSettingsResult> = Observable.empty()

   override fun observeLocationSettingState(): Observable<Boolean> = Observable.just(true)

   override fun detectLastKnownLocation(): Observable<Location>
         = if (location == null) Observable.empty() else Observable.just(location)
}
