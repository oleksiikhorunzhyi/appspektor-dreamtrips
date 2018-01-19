package com.worldventures.wallet.ui.settings.security.offline_mode

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface WalletOfflineModeSettingsPresenter : WalletPresenter<WalletOfflineModeSettingsScreen> {

   fun goBack()

   fun switchOfflineMode()

   fun switchOfflineModeCanceled()

   fun navigateToSystemSettings()

   fun fetchOfflineModeState()
}
