package com.worldventures.dreamtrips.wallet.ui.wizard.welcome;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface WizardWelcomeScreen extends WalletScreen {

   void userName(String userName);

   void welcomeMessage(User user);

   void userPhoto(String photoUrl);

   void showAnimation();

   ProvisioningMode getProvisionMode();
}
