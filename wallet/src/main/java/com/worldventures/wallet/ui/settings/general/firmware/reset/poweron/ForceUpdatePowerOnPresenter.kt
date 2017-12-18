package com.worldventures.wallet.ui.settings.general.firmware.reset.poweron

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface ForceUpdatePowerOnPresenter : WalletPresenter<ForceUpdatePowerOnScreen> {

   fun onBack()

   fun goNext()
}
