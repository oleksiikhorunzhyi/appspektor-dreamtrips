package com.worldventures.dreamtrips.api.flagging.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface FlagReason extends Identifiable<Integer> {

    @SerializedName("name")
    String name();
    @SerializedName("require_description")
    boolean requireDescription();

}
