package com.worldventures.dreamtrips.wallet.domain.entity;


import org.immutables.value.Value;

@Value.Immutable
public interface SmartCardDetails {

   String smartCardId();
   String bleAddress();

}
