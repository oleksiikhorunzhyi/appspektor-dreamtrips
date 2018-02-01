package com.worldventures.wallet.ui.wizard.input.scanner

import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import com.worldventures.wallet.ui.wizard.input.helper.InputDelegateView
import io.techery.janet.operationsubscriber.view.OperationView

interface WizardScanBarcodeScreen : WalletScreen, InputDelegateView {

   fun startCamera()

   fun showRationaleForCamera()

   fun showDeniedForCamera()

   fun reset()

   override fun provideOperationFetchSmartCardUser(): OperationView<SmartCardUserCommand>
}
