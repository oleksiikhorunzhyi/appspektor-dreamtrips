package com.worldventures.dreamtrips.api.settings.model;

import org.immutables.value.Value;

@Value.Immutable
public abstract class UnknownSetting implements Setting<String> {

    public static UnknownSetting create(String name) {
        return ImmutableUnknownSetting.builder().name(name).value("").build();
    }

    @Value.Derived
    @Override
    public Type type() {
        return Type.UNKNOWN;
    }
}
