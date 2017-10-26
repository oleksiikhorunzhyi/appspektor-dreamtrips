package com.worldventures.wallet.ui.settings.general.firmware.install;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletInstallFirmwarePresenter extends WalletPresenter<WalletInstallFirmwareScreen> {

   void retry();

   void cancelReinstall();

   void install();
}
