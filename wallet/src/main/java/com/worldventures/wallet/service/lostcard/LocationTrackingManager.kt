package com.worldventures.wallet.service.lostcard

import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.modules.auth.service.AuthInteractor
import com.worldventures.core.service.location.DetectLocationService
import com.worldventures.wallet.service.SmartCardLocationInteractor
import com.worldventures.wallet.service.lostcard.command.FetchTrackingStatusCommand
import io.techery.janet.Command
import rx.Observable
import rx.Subscription
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class LocationTrackingManager internal constructor(private val locationInteractor: SmartCardLocationInteractor,
                                                   private val smartCardIdHelper: SmartCardIdHelper,
                                                   private val locationService: DetectLocationService,
                                                   private val authInteractor: AuthInteractor,
                                                   private val lostCardManager: LostCardManager,
                                                   private val sessionHolder: SessionHolder) {

   private var trackSubscription: Subscription? = null
   private val lostCardStarted = AtomicBoolean(false)

   fun track() {
      if (trackSubscription != null) {
         return
      }

      trackSubscription = Observable.combineLatest(
            locationService.observeLocationSettingState().startWith(locationService.isEnabled),
            observeTrackingStatus(),
            observeUserSession(),
            smartCardIdHelper.smartCardIdObservable()
      ) { locationEnabled, trackingEnabled, userSession, smartCardId -> Pair(smartCardId, userSession != null && locationEnabled && trackingEnabled) }
            .distinctUntilChanged()
            .subscribe({ (smartCardId, trackingEnabled) -> handleTrackingStatus(smartCardId, trackingEnabled) }) { throwable -> Timber.e(throwable) }

      locationInteractor.fetchTrackingStatusPipe().send(FetchTrackingStatusCommand())
   }

   @Suppress("UnsafeCallOnNullableType")
   private fun handleTrackingStatus(smartCardId: String?, isEnabled: Boolean) {
      val shouldConnect = smartCardId != null && isEnabled
      synchronized(lostCardStarted) {
         if (shouldConnect == lostCardStarted.get()) return

         if (shouldConnect) {
            lostCardManager.connect(smartCardId!!)
         } else {
            lostCardManager.disconnect()
         }
         lostCardStarted.set(shouldConnect)
      }
   }

   private fun observeTrackingStatus(): Observable<Boolean> {
      return Observable.merge<Command<Boolean>>(
            locationInteractor.updateTrackingStatusPipe().observeSuccess(),
            locationInteractor.fetchTrackingStatusPipe().observeSuccess()
      )
            .map { it.result }
   }

   private fun observeUserSession(): Observable<UserSession?> {
      return Observable.merge(
            authInteractor.logoutPipe().observeSuccess().map { null },
            authInteractor.loginActionPipe().observeSuccess()
                  .doOnNext { smartCardIdHelper.fetchSmartCardFromServer(it.result) }
                  .map { it.result }
                  .startWith(sessionHolder.get().orNull())
                  .filter { session -> session != null }
      )
   }
}
