package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface Provision {
    String memberId();

    String userSecret();
}