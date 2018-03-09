package com.worldventures.wallet.ui.wizard.profile

import com.worldventures.wallet.service.command.SetupUserDataCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfilePhotoView

import io.techery.janet.operationsubscriber.view.OperationView

interface WizardEditProfileScreen : WalletScreen, WalletProfilePhotoView {

   var profile: ProfileViewModel

   val provisionMode: ProvisioningMode

   fun provideOperationView(): OperationView<SetupUserDataCommand>

   fun showConfirmationDialog(profileViewModel: ProfileViewModel)
}
