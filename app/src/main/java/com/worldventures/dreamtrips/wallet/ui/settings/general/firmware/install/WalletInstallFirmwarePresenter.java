package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletInstallFirmwarePresenter extends WalletPresenterI<WalletInstallFirmwareScreen> {

   void retry();

   void cancelReinstall();

}
