package com.worldventures.dreamtrips.wallet.ui.common.navigation;


public interface NavigatorConductor {

   void goGeneralSettings();

   void goProvisioningBlocked();

   void goCardList();

   void goInstallFirmware();

   void goNewFirmwareAvailable();

   void goWizardWelcome();

   void goBack();

   void finish();
}
