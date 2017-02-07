package com.worldventures.dreamtrips.wallet.domain.entity.lostcard;

import org.immutables.value.Value;

@Value.Immutable
public interface WalletAddress {

   String addressLine();

   String countryName();

   String subAdminArea();

   String postalCode();

}
