package com.worldventures.dreamtrips.api.smart_card.terms_and_condition.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface TermsAndConditions {

    @SerializedName("link")
    String url();
    @SerializedName("version")
    int version();

}
