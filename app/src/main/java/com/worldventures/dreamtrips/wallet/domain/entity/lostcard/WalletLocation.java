package com.worldventures.dreamtrips.wallet.domain.entity.lostcard;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Value.Immutable
public interface WalletLocation {

   WalletCoordinates coordinates();

   @Nullable
   Date postedAt();

   Date createdAt();

   WalletLocationType type();
}
