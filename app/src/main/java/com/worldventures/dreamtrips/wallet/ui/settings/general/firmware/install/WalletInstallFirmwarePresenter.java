package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletInstallFirmwarePresenter extends WalletPresenter<WalletInstallFirmwareScreen> {

   void retry();

   void cancelReinstall();

}
