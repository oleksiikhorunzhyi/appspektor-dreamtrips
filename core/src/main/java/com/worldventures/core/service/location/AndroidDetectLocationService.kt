package com.worldventures.core.service.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v4.content.PermissionChecker.checkSelfPermission
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import rx.Emitter
import rx.Observable
import rx.Observable.unsafeCreate

private const val INTERVAL = 4500

internal class AndroidDetectLocationService(private val context: Context) : DetectLocationService {

   @Suppress("UnsafeCast")
   private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

   override fun isPermissionGranted(): Boolean {
      return checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
   }

   override fun isEnabled(): Boolean {
      return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
   }

   private fun provideLocationRequest(): LocationRequest {
      return LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1)
            .setFastestInterval(INTERVAL.toLong())
            .setInterval(INTERVAL.toLong())
   }

   override fun fetchLastKnownLocationSettings(): Observable<SettingsResult> {
      return Observable.create({ emitter ->
         LocationServices.getSettingsClient(context).checkLocationSettings(LocationSettingsRequest.Builder()
               .addLocationRequest(provideLocationRequest())
               .setAlwaysShow(true)
               .build())
               .addOnSuccessListener { emitter.onNext(SettingsResult(response = it)) }
               .addOnFailureListener { emitter.onNext(SettingsResult(exception = it)) }
               .addOnCompleteListener { emitter.onCompleted() }
      }, Emitter.BackpressureMode.NONE)
   }

   override fun observeLocationSettingState(): Observable<Boolean> {
      val adapter = RxLocationAdapter(context)
      return unsafeCreate(adapter)
            .map { isEnabled }
            .distinctUntilChanged()
            .doOnUnsubscribe { adapter.release() }
   }

   override fun detectLastKnownLocation(): Observable<Location> {
      return Observable.create<Location>({ emitter ->
         LocationServices.getFusedLocationProviderClient(context)
               .lastLocation
               .addOnSuccessListener { emitter.onNext(it) }
               .addOnFailureListener { emitter.onError(it) }
               .addOnCompleteListener { emitter.onCompleted() }
      }, Emitter.BackpressureMode.NONE)
   }

}
