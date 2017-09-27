package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface WalletSuccessInstallFirmwareScreen extends WalletScreen {
   void setSubTitle(String version);

   FirmwareUpdateData getFirmwareUpdateData();
}