package com.worldventures.wallet.ui.wizard.assign

import com.worldventures.wallet.service.command.wizard.AddDummyRecordCommand
import com.worldventures.wallet.service.command.wizard.WizardCompleteCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.base.screen.WalletScreen

import io.techery.janet.operationsubscriber.view.OperationView

interface WizardAssignUserScreen : WalletScreen {

   val provisionMode: ProvisioningMode

   fun provideOperationView(): OperationView<WizardCompleteCommand>

   fun provideDummyRecordOperationView(): OperationView<AddDummyRecordCommand>
}
