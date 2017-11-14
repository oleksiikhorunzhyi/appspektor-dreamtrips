package com.worldventures.wallet.ui.settings.general.firmware.reset.poweron;

import android.view.View;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

public interface ForceUpdatePowerOnScreen extends WalletScreen {

   void setButtonAction(View.OnClickListener onClickListener);

   void showDialogEnableBleAndInternet();

}
