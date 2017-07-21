package com.worldventures.dreamtrips.wallet.ui.records.swiping;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface WizardChargingScreen extends WalletScreen {

   void showSwipeError();

   void trySwipeAgain();

   void showSwipeSuccess();

   void userPhoto(@Nullable SmartCardUserPhoto photo);
}
