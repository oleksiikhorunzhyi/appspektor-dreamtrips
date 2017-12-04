package com.worldventures.wallet.ui.wizard.checking

import com.worldventures.wallet.ui.common.base.screen.WalletScreen

interface WizardCheckingScreen : WalletScreen {

   fun networkAvailable(available: Boolean)

   fun bluetoothEnable(enable: Boolean)

   fun bluetoothDoesNotSupported()

   fun buttonEnable(enable: Boolean)
}
