package com.worldventures.dreamtrips.api.settings.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.util.Preconditions;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

import rx.functions.Func0;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SelectSetting implements Setting<String> {

    @SerializedName("options")
    public abstract List<String> options();

    @Value.Derived
    @Override
    public Type type() {
        return Type.SELECT;
    }

    @Value.Check
    protected void check() {
        Preconditions.check(new Func0<Boolean>() {
            @Override
            public Boolean call() {
                return !options().isEmpty();
            }
        }, "Options are not allowed to be empty");
    }

}
