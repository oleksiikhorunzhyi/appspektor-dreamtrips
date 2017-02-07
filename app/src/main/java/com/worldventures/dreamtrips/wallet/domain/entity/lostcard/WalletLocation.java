package com.worldventures.dreamtrips.wallet.domain.entity.lostcard;

import org.immutables.value.Value;

import java.util.Date;

@Value.Immutable
public interface WalletLocation {

   WalletCoordinates coordinates();

   Date postedAt();

   Date createdAt();

   WalletLocationType type();

   String name();
}
