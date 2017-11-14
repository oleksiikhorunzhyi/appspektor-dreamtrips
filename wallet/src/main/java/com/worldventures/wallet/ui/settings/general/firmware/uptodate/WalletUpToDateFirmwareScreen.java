package com.worldventures.wallet.ui.settings.general.firmware.uptodate;

import android.support.annotation.Nullable;

import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

public interface WalletUpToDateFirmwareScreen extends WalletScreen {
   void version(@Nullable SmartCardFirmware version);
}