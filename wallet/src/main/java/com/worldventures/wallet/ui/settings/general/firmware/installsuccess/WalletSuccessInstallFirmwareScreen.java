package com.worldventures.wallet.ui.settings.general.firmware.installsuccess;

import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

public interface WalletSuccessInstallFirmwareScreen extends WalletScreen {
   void setSubTitle(String version);

   FirmwareUpdateData getFirmwareUpdateData();
}