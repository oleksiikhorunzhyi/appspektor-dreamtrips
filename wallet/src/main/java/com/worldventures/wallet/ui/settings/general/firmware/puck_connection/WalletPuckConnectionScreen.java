package com.worldventures.wallet.ui.settings.general.firmware.puck_connection;

import android.support.annotation.Nullable;

import com.worldventures.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

public interface WalletPuckConnectionScreen extends WalletScreen {
   void userPhoto(@Nullable SmartCardUserPhoto photo);
}
