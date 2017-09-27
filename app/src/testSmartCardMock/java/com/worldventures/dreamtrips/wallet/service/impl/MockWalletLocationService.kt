package com.worldventures.dreamtrips.wallet.service.impl

import android.location.Location
import com.google.android.gms.location.LocationSettingsResult
import com.worldventures.dreamtrips.wallet.service.location.WalletDetectLocationService
import rx.Observable

class MockWalletLocationService: WalletDetectLocationService {

   override fun isPermissionGranted(): Boolean = true

   override fun isEnabled(): Boolean = true

   override fun fetchLastKnownLocationSettings(): Observable<LocationSettingsResult> = Observable.empty()

   override fun observeLocationSettingState(): Observable<Boolean> = Observable.just(true)

   override fun detectLastKnownLocation(): Observable<Location> = Observable.empty()
}