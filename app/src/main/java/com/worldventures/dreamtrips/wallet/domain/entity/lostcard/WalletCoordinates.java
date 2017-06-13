package com.worldventures.dreamtrips.wallet.domain.entity.lostcard;

import org.immutables.value.Value;

@Value.Immutable
public interface WalletCoordinates {

   Double lat();

   Double lng();
}
