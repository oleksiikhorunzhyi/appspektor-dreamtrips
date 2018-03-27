package com.worldventures.wallet.ui.settings.security.offline_mode

import com.worldventures.wallet.service.command.offline_mode.SwitchOfflineModeCommand
import com.worldventures.wallet.ui.common.base.screen.WalletScreen

import io.techery.janet.operationsubscriber.view.OperationView
import rx.Observable

interface WalletOfflineModeSettingsScreen : WalletScreen {

   fun observeOfflineModeSwitcher(): Observable<Boolean>

   fun provideOperationView(): OperationView<SwitchOfflineModeCommand>

   fun showConfirmationDialog(enable: Boolean)

   fun setOfflineModeState(enabled: Boolean)
}
