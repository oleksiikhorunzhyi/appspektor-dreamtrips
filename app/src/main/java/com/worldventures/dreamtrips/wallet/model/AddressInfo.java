package com.worldventures.dreamtrips.wallet.model;

import org.immutables.value.Value;

@Value.Immutable
public interface AddressInfo {

    String address1();

    String address2();

    String city();

    String state();

    String zip();

}
