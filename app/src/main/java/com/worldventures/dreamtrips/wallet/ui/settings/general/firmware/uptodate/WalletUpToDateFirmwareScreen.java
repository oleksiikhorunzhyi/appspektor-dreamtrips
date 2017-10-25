package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface WalletUpToDateFirmwareScreen extends WalletScreen {
   void version(@Nullable SmartCardFirmware version);
}