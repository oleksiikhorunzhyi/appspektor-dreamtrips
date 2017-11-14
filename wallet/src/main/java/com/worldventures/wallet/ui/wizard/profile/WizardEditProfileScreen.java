package com.worldventures.wallet.ui.wizard.profile;

import android.support.annotation.Nullable;

import com.worldventures.wallet.service.command.SetupUserDataCommand;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WizardEditProfileScreen extends WalletScreen, WalletProfilePhotoView {

   OperationView<SetupUserDataCommand> provideOperationView();

   void setProfile(ProfileViewModel model);

   ProfileViewModel getProfile();

   void showConfirmationDialog(ProfileViewModel profileViewModel);

   @Nullable
   ProvisioningMode getProvisionMode();
}
