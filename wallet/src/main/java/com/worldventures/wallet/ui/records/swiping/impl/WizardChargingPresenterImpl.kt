package com.worldventures.wallet.ui.records.swiping.impl

import com.crashlytics.android.Crashlytics
import com.worldventures.wallet.analytics.ConnectFlyeToChargerAction
import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.domain.entity.SDKRecord
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.records.add.toCreateRecordBundle
import com.worldventures.wallet.ui.records.swiping.WizardChargingPresenter
import com.worldventures.wallet.ui.records.swiping.WizardChargingScreen
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction
import io.techery.janet.smartcard.event.CardSwipedEvent
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class WizardChargingPresenterImpl(navigator: Navigator,
                                  deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                  private val smartCardInteractor: SmartCardInteractor,
                                  private val analyticsInteractor: WalletAnalyticsInteractor)
   : WalletPresenterImpl<WizardChargingScreen>(navigator, deviceConnectionDelegate), WizardChargingPresenter {

   override fun attachView(view: WizardChargingScreen) {
      super.attachView(view)
      trackScreen()
      fetchUserPhoto()
      observeCharger()

      //observeConnectionStatus();
      //was developed in scope of SMARTCARD-1516
      //commented due to bug SMARTCARD-1792,
      //TODO: uncomment by request in future
   }

   private fun fetchUserPhoto() {
      smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
               val user = it.result
               if (user == null) {
                  val message = String.format("User is null in SmartCardUserCommand storage in %s screen",
                        javaClass.simpleName)
                  Timber.e(message)
                  Crashlytics.log(message)
               } else {
                  view.userPhoto(user.userPhoto)
               }
            }, {
               Timber.e(it)
               Crashlytics.logException(it)
            })
   }

   private fun observeCharger() {
      smartCardInteractor.startCardRecordingPipe()
            .createObservable(StartCardRecordingAction())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationStartCardRecording()).create())

      smartCardInteractor.cardSwipedEventPipe()
            .observeSuccess()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
               if (event.result == CardSwipedEvent.Result.ERROR) {
                  view.showSwipeError()
               } else {
                  view.showSwipeSuccess()
               }
            }

      smartCardInteractor.chargedEventPipe()
            .observeSuccess()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .map { cardChargedEvent -> cardChargedEvent.record }
            .subscribe({ this.cardSwiped(it) }, { this.errorReceiveRecord() })
   }

   private fun errorReceiveRecord() {
      view.trySwipeAgain()
   }

   private fun trackScreen() {
      analyticsInteractor.walletAnalyticsPipe()
            .send(WalletAnalyticsCommand(ConnectFlyeToChargerAction()))
   }

   override fun detachView(retainInstance: Boolean) {
      super.detachView(retainInstance)
      smartCardInteractor.stopCardRecordingPipe().send(StopCardRecordingAction())
   }

   override fun goBack() {
      navigator.goBack()
   }

   private fun cardSwiped(card: SDKRecord) {
      navigator.goAddCard(toCreateRecordBundle(card))
   }
}
