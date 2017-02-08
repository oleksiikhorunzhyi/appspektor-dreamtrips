package com.worldventures.dreamtrips.wallet.domain.entity.lostcard;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
public interface WalletAddress {

   String addressLine();

   String countryName();

   @Nullable
   String subAdminArea();

   @Nullable
   String postalCode();

}
