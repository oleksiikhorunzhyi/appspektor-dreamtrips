package com.worldventures.wallet.ui.settings.general.firmware.newavailable;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletNewFirmwareAvailablePresenter extends WalletPresenter<WalletNewFirmwareAvailableScreen> {

   void goBack();

   void openMarket();

   void downloadButtonClicked();

   void openSettings();

   void fetchFirmwareInfo();
}
