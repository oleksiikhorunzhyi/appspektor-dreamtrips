package com.worldventures.dreamtrips.wallet.domain.entity;


import org.immutables.value.Value;

import java.util.Date;

@Value.Immutable
public interface SmartCardDetails {

   String serialNumber();

   long smartCardId();

   String bleAddress();

   String wvOrderId();

   String revVersion();

   String nxtOrderId();

   Date orderDate();

}
