package com.worldventures.dreamtrips.api.api_common.model;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

public interface HasLanguage {
    @SerializedName("language")
    @Nullable
    String language();
}
