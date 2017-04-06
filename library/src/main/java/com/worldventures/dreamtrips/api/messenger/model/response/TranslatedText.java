package com.worldventures.dreamtrips.api.messenger.model.response;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface TranslatedText {

    @SerializedName("text")
    String text();

}
