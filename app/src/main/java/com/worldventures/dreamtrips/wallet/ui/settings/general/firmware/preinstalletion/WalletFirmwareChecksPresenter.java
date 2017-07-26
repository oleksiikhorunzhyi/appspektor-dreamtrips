package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletFirmwareChecksPresenter extends WalletPresenter<WalletFirmwareChecksScreen> {

   void goBack();

   void installLater();

   void install();

}
