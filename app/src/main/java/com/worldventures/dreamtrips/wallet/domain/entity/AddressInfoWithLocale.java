package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;

import java.util.Locale;

@Value.Immutable
public interface AddressInfoWithLocale {

    AddressInfo addressInfo();

    Locale locale();
}
