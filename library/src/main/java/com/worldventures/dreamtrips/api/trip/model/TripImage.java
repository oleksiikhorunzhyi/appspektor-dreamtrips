package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public abstract class TripImage implements Identifiable<String> {

    @SerializedName("url")
    public abstract String url();
    @SerializedName("origin_url")
    public abstract String originUrl();
    @SerializedName("type")
    public abstract String type();
    @Nullable
    @SerializedName("description")
    public abstract String description();

    @Value.Derived
    boolean containsType(Type... type) {
        for (Type t : type) {
            if (!type().contains(t.toString())) return false;
        }
        return true;
    }

    public enum Type {
        RETINA, NORMAL, THUMB
    }

}
