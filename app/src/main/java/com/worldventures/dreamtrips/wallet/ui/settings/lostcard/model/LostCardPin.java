package com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;

import org.immutables.value.Value;

@Value.Immutable
public interface LostCardPin {

   @Nullable
   String place();

   @Nullable
   String address();

   WalletCoordinates position();
}
