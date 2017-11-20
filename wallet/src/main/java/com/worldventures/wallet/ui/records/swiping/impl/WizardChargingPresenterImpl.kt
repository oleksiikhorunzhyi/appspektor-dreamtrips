package com.worldventures.wallet.ui.records.swiping.impl

import com.worldventures.wallet.analytics.ConnectFlyeToChargerAction
import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.http.CreateRecordCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.records.swiping.WizardChargingPresenter
import com.worldventures.wallet.ui.records.swiping.WizardChargingScreen
import com.worldventures.wallet.util.WalletRecordUtil

import io.techery.janet.helper.ActionStateSubscriber
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction
import io.techery.janet.smartcard.event.CardSwipedEvent
import rx.android.schedulers.AndroidSchedulers

// TODO: 5/30/17 Create task and refactor both screen and presenter, it's ugly and error handling is a joke
class WizardChargingPresenterImpl(navigator: Navigator,
                                  deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                  private val smartCardInteractor: SmartCardInteractor,
                                  private val recordInteractor: RecordInteractor,
                                  private val analyticsInteractor: WalletAnalyticsInteractor)
   : WalletPresenterImpl<WizardChargingScreen>(navigator, deviceConnectionDelegate), WizardChargingPresenter {

   override fun attachView(view: WizardChargingScreen) {
      super.attachView(view)
      trackScreen()
      fetchUserPhoto()
      observeCharger()
      observeBankCardCreation()

      //observeConnectionStatus();
      //was developed in scope of SMARTCARD-1516
      //commented due to bug SMARTCARD-1792,
      //TODO: uncomment by request in future
   }

   private fun fetchUserPhoto() {
      smartCardInteractor.smartCardUserPipe()
            .createObservable(SmartCardUserCommand.fetch())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ActionStateSubscriber<SmartCardUserCommand>()
                  .onSuccess { command -> view.userPhoto(command.result.userPhoto) }
            )
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

   private fun observeBankCardCreation() {
      recordInteractor.bankCardPipe()
            .observe()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationCreateRecord())
                  .onSuccess { command -> bankCardCreated(command.result) }
                  .create())
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

   private fun cardSwiped(card: io.techery.janet.smartcard.model.Record) {
      recordInteractor.bankCardPipe().send(CreateRecordCommand(card))
   }

   private fun bankCardCreated(record: Record) {
      navigator.goAddCard(WalletRecordUtil.prepareRecordViewModel(record))
   }
}
