package com.worldventures.wallet.ui.settings;

import android.support.annotation.Nullable;
import android.view.View;

import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import java.util.Date;
import java.util.List;

public interface WalletSettingsScreen extends WalletScreen {

   void smartCardGeneralStatus(@Nullable SmartCardFirmware version, int batteryLevel, Date lastSync);

   void firmwareUpdateCount(int count);

   List<View> getToggleableItems();
}
