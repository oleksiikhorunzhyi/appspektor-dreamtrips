package com.worldventures.dreamtrips.api.settings.model;

import com.google.gson.annotations.SerializedName;

public interface Setting<T> {

    @SerializedName("name")
    String name();
    @SerializedName("value")
    T value();

    Type type();

    enum Type {
        FLAG, SELECT,

        UNKNOWN
    }
}
