package com.worldventures.wallet.ui.wizard.pairkey.impl

import com.worldventures.core.janet.composer.ActionPipeCacheWiper
import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.wizard.CardConnectedAction
import com.worldventures.wallet.analytics.wizard.CheckFrontAction
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.command.ConnectSmartCardCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyPresenter
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyScreen
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class PairKeyPresenterImpl(navigator: Navigator,
                           deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                           private val pairingHelper: PairKeyNavigationHelper,
                           private val smartCardInteractor: SmartCardInteractor,
                           private val analyticsInteractor: WalletAnalyticsInteractor)
   : WalletPresenterImpl<PairKeyScreen>(navigator, deviceConnectionDelegate), PairKeyPresenter {

   private lateinit var pairDelegate: PairDelegate

   override fun attachView(view: PairKeyScreen) {
      super.attachView(view)
      this.pairDelegate = PairDelegate.create(getView().provisionMode, navigator, smartCardInteractor)
      pairDelegate.prepareView(getView())
      analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(CheckFrontAction()))
      observePairing(view)
      observeConnection()
      observeDisconnect()
   }

   private fun observePairing(view: PairKeyScreen) {
      pairingHelper.failedPairing = {
         view.showPairingError()
      }

      pairingHelper.successPairing = {
         pairDelegate.cardConnected(view, it)
         analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(CardConnectedAction(it)))
      }
   }

   private fun observeConnection() {
      smartCardInteractor.connectActionPipe()
            .observeWithReplay()
            .compose(ActionPipeCacheWiper(smartCardInteractor.connectActionPipe()))
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationCreateAndConnect())
                  .onSuccess { pairingHelper.onConnect(it.smartCardId) }
                  .create())
   }

   private fun observeDisconnect() {
      smartCardInteractor.disconnectPipe()
            .observeSuccessWithReplay()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pairingHelper.onDisconnect() }, { Timber.e(it) })
   }

   override fun tryToPairAndConnectSmartCard() {
      view.nextButtonEnable(false)
      smartCardInteractor.connectActionPipe().send(ConnectSmartCardCommand(view.barcode))
   }

   override fun detachView(retainInstance: Boolean) {
      pairingHelper.onViewDetach()
      super.detachView(retainInstance)
   }

   override fun goBack() {
      navigator.goBack()
   }
}
