package com.worldventures.wallet.ui.wizard.pairkey.impl

import com.worldventures.core.janet.composer.ActionPipeCacheWiper
import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.wizard.CardConnectedAction
import com.worldventures.wallet.analytics.wizard.CheckFrontAction
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.service.command.CreateAndConnectToCardCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyPresenter
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyScreen

import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class PairKeyPresenterImpl(navigator: Navigator, deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                           private val smartCardInteractor: SmartCardInteractor, private val wizardInteractor: WizardInteractor,
                           private val analyticsInteractor: WalletAnalyticsInteractor) : WalletPresenterImpl<PairKeyScreen>(navigator, deviceConnectionDelegate), PairKeyPresenter {

   private lateinit var pairDelegate: PairDelegate

   override fun attachView(view: PairKeyScreen) {
      super.attachView(view)
      this.pairDelegate = PairDelegate.create(getView().provisionMode, navigator, smartCardInteractor)
      pairDelegate.prepareView(getView())
      analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(CheckFrontAction()))
      observeCreateAndConnectSmartCard()
   }

   private fun observeCreateAndConnectSmartCard() {
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ActionPipeCacheWiper(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideOperationCreateAndConnect())
                  .onSuccess { smartCardConnected() }
                  .onFail { _, throwable ->
                     Timber.e(throwable)
                     view.nextButtonEnable(true)
                  }
                  .create())
   }

   private fun smartCardConnected() {
      pairDelegate.navigateOnNextScreen(view)
      analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(CardConnectedAction()))
   }

   override fun tryToPairAndConnectSmartCard() {
      view.nextButtonEnable(false)
      wizardInteractor.createAndConnectActionPipe().send(CreateAndConnectToCardCommand(view.barcode))
   }

   override fun goBack() {
      navigator.goBack()
   }
}
