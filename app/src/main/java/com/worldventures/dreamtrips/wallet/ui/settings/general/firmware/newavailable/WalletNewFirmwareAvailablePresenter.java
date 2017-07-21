package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable;

import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletNewFirmwareAvailablePresenter extends WalletPresenterI<WalletNewFirmwareAvailableScreen> {

   void goBack();

   void openMarket();

   void downloadButtonClicked();

   void openSettings();

   void fetchFirmwareInfo();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

}
