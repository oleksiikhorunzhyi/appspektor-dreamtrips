package com.worldventures.dreamtrips.api.api_common.model;

import com.google.gson.annotations.SerializedName;

public interface Identifiable<T> {
    @SerializedName("id")
    T id();
}
