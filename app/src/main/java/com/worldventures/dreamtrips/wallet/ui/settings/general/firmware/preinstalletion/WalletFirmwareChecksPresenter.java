package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletFirmwareChecksPresenter extends WalletPresenterI<WalletFirmwareChecksScreen> {

   void goBack();

   void installLater();

   void install();

}
