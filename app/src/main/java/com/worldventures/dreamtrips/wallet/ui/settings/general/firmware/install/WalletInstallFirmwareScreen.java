package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface WalletInstallFirmwareScreen extends WalletScreen, OperationScreen {

   void showInstallingStatus(int currentStep, int totalSteps, int progress);

   void setInstallStarted(boolean started);

   boolean isInstallStarted();
}