package com.worldventures.dreamtrips.api.settings.model;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class FlagSetting implements Setting<Boolean> {

    @Value.Derived
    @Override
    public Type type() {
        return Type.FLAG;
    }
}
