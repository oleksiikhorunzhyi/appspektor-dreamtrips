package com.worldventures.wallet.ui.settings.general.firmware.start;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface StartFirmwareInstallPresenter extends WalletPresenter<StartFirmwareInstallScreen> {

   void goBack();

   void finish();

   void prepareForUpdate();
}
