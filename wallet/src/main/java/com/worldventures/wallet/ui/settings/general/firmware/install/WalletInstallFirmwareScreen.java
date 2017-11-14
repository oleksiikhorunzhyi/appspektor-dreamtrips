package com.worldventures.wallet.ui.settings.general.firmware.install;

import com.worldventures.wallet.service.firmware.command.InstallFirmwareCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletInstallFirmwareScreen extends WalletScreen {

   void showInstallingStatus(int currentStep, int totalSteps, int progress);

   void setInstallStarted(boolean started);

   boolean isInstallStarted();

   OperationView<InstallFirmwareCommand> provideOperationInstall();
}