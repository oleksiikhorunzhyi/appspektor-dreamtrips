package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface WalletProfilePhoneScreen extends WalletScreen{

   @Nullable
   SmartCardUserPhone userPhone();
}
