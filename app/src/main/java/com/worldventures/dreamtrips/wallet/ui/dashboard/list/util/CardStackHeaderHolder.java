package com.worldventures.dreamtrips.wallet.ui.dashboard.list.util;

import android.support.annotation.Nullable;

import com.techery.spares.adapter.HeaderItem;
import com.worldventures.dreamtrips.wallet.domain.entity.Firmware;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import org.immutables.value.Value;

@Value.Immutable
public abstract class CardStackHeaderHolder implements HeaderItem {

   @Nullable
   public abstract SmartCard smartCard();

   @Value.Default
   public Firmware firmware() {
      return ImmutableFirmware.builder().updateAvailable(false).build();
   }

   @Value.Default
   public int cardCount() { return 0;}
}
