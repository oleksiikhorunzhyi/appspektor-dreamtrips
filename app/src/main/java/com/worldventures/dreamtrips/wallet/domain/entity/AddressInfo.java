package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;

@Value.Immutable
public interface AddressInfo {

    String address1();

    String address2();

    String city();

    String state();

    String zip();

}
