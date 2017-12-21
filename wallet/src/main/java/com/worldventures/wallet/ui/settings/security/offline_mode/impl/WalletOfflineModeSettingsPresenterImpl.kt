package com.worldventures.wallet.ui.settings.security.offline_mode.impl

import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.settings.SettingsOfflineModeScreenAction
import com.worldventures.wallet.analytics.settings.SettingsOfflineModeStateChangeAction
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.command.offline_mode.OfflineModeStatusCommand
import com.worldventures.wallet.service.command.offline_mode.SwitchOfflineModeCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsPresenter
import com.worldventures.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsScreen
import com.worldventures.wallet.util.GuaranteedProgressVisibilityTransformer
import com.worldventures.wallet.util.NetworkUnavailableException
import io.techery.janet.ActionState
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers

class WalletOfflineModeSettingsPresenterImpl(navigator: Navigator,
                                             deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                             private val smartCardInteractor: SmartCardInteractor,
                                             private val networkDelegate: WalletNetworkDelegate,
                                             private val analyticsInteractor: WalletAnalyticsInteractor)
   : WalletPresenterImpl<WalletOfflineModeSettingsScreen>(navigator, deviceConnectionDelegate), WalletOfflineModeSettingsPresenter {

   private var waitingForNetwork = false

   override fun attachView(view: WalletOfflineModeSettingsScreen) {
      super.attachView(view)
      trackScreen()
      networkDelegate.setup(view)

      observeOfflineModeState()
      observeOfflineModeSwitcher()

      observeNetworkState()
      fetchOfflineModeState()
   }

   override fun goBack() {
      navigator.goBack()
   }

   override fun fetchOfflineModeState() {
      smartCardInteractor.offlineModeStatusPipe().send(OfflineModeStatusCommand.fetch())
   }

   override fun switchOfflineMode() {
      smartCardInteractor.switchOfflineModePipe().send(SwitchOfflineModeCommand())
   }

   override fun switchOfflineModeCanceled() {
      waitingForNetwork = false
      fetchOfflineModeState()
   }

   override fun navigateToSystemSettings() {
      navigator.goSystemSettings()
   }

   private fun observeOfflineModeState() {
      smartCardInteractor.offlineModeStatusPipe()
            .observeSuccess()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.result }
            .subscribe { view.setOfflineModeState(it) }

      smartCardInteractor.switchOfflineModePipe()
            .observe()
            .compose(GuaranteedProgressVisibilityTransformer<ActionState<SwitchOfflineModeCommand>>())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationView())
                  .onProgress { _, _ -> waitingForNetwork = false }
                  .onSuccess { trackStateChange(it.result) }
                  .onFail { _, throwable ->
                     if (throwable.cause is NetworkUnavailableException) {
                        waitingForNetwork = true
                     }
                  }
                  .create()
            )
   }

   private fun observeOfflineModeSwitcher() {
      view.observeOfflineModeSwitcher()
            .compose(view.bindUntilDetach())
            .subscribe { this.onOfflineModeSwitcherChanged(it) }
   }

   private fun observeNetworkState() {
      networkDelegate.observeConnectedState()
            .compose(view.bindUntilDetach())
            .filter { it && waitingForNetwork }
            .subscribe { switchOfflineMode() }
   }

   private fun onOfflineModeSwitcherChanged(enabled: Boolean) {
      view.showConfirmationDialog(enabled)
   }

   private fun trackStateChange(isOfflineModeEnabled: Boolean) {
      analyticsInteractor.walletAnalyticsPipe()
            .send(WalletAnalyticsCommand(SettingsOfflineModeStateChangeAction(isOfflineModeEnabled)))
   }

   private fun trackScreen() {
      smartCardInteractor.offlineModeStatusPipe()
            .observeSuccess()
            .take(1)
            .map { it.result }
            .subscribe { analyticsInteractor.walletAnalyticsPipe()
                     .send(WalletAnalyticsCommand(SettingsOfflineModeScreenAction(it)))
            }
   }
}
