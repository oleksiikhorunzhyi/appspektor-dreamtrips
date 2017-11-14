package com.worldventures.wallet.ui.wizard.welcome;

import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.smartcard.model.User;

public interface WizardWelcomeScreen extends WalletScreen {

   void userName(String userName);

   void welcomeMessage(User.MemberStatus memberStatus);

   void userPhoto(String photoUrl);

   void showAnimation();

   ProvisioningMode getProvisionMode();
}
