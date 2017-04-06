package com.worldventures.dreamtrips.api.messenger.model.request;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface TranslateTextBody {

    @SerializedName("text")
    String text();
    @SerializedName("to")
    String toLanguage();

}
