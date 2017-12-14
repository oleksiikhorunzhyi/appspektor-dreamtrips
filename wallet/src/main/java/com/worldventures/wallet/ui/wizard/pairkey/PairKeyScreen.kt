package com.worldventures.wallet.ui.wizard.pairkey

import com.worldventures.wallet.service.command.CreateAndConnectToCardCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.base.screen.WalletScreen

import io.techery.janet.operationsubscriber.view.OperationView

interface PairKeyScreen : WalletScreen, PairView {

   val provisionMode: ProvisioningMode

   val barcode: String

   fun provideOperationCreateAndConnect(): OperationView<CreateAndConnectToCardCommand>

   fun nextButtonEnable(enable: Boolean)
}
