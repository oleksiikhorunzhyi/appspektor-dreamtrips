package com.worldventures.dreamtrips.wallet.ui.dashboard.util;

import android.support.annotation.Nullable;

import com.techery.spares.adapter.HeaderItem;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

import org.immutables.value.Value;

@Value.Immutable
public abstract class CardStackHeaderHolder implements HeaderItem {

   @Nullable
   public abstract SmartCard smartCard();

   @Nullable
   public abstract SmartCardStatus smartCardStatus();

   @Nullable
   public abstract SmartCardUser smartCardUser();

   @Value.Default
   public boolean firmwareUpdateAvailable() {
      return false;
   }

   @Value.Default
   public int cardCount() { return 0;}
}
