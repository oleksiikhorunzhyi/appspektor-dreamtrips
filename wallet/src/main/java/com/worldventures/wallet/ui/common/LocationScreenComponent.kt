package com.worldventures.wallet.ui.common

import android.app.Activity
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.worldventures.core.service.location.SettingsResult

import java.lang.ref.WeakReference

import rx.Observable
import rx.subjects.PublishSubject
import timber.log.Timber

class LocationScreenComponent(activity: Activity) {

   private val resultPublishSubject = PublishSubject.create<EnableResult>()
   private val activityReference: WeakReference<Activity> = WeakReference(activity)
   private var resolutionIsShown: Boolean = false

   fun checkSettingsResult(settingsResult: SettingsResult): Observable<EnableResult> {
      settingsResult.exception?.let {
         when (it) {
            is ResolvableApiException -> processResolvableApiError(it)
         }
      }
      settingsResult.response?.let { notifyCallbacks(EnableResult.AVAILABLE) }
      return resultPublishSubject.asObservable().take(1)
   }

   private fun processResolvableApiError(error: ResolvableApiException) {
      try {
         if (resolutionIsShown) return
         val activity = activityReference.get()
         if (activity != null) {
            error.startResolutionForResult(activity, REQUEST_CODE)
            resolutionIsShown = true
         }
      } catch (e: IntentSender.SendIntentException) {
         Timber.e(e)
      }
   }

   private fun notifyCallbacks(enableResult: EnableResult) {
      resultPublishSubject.onNext(enableResult)
   }

   fun onActivityResult(requestCode: Int, resultCode: Int): Boolean {
      if (requestCode == REQUEST_CODE) {
         resolutionIsShown = false
         notifyCallbacks(if (resultCode == Activity.RESULT_OK) EnableResult.AVAILABLE else EnableResult.UNAVAILABLE)
         return true
      }
      return false
   }

   enum class EnableResult {
      AVAILABLE, UNAVAILABLE
   }

   companion object {

      val COMPONENT_NAME = "com.worldventures.core.service.location.LocationSettingsService"

      private val REQUEST_CODE = 0xFF01
   }
}
