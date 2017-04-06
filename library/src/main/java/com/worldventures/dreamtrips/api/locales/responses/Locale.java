package com.worldventures.dreamtrips.api.locales.responses;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.HasLanguage;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Locale extends HasLanguage {
    @SerializedName("locale_name")
    String localeName();
    @SerializedName("country")
    String country();
}
