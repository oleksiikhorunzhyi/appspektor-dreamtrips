package com.worldventures.dreamtrips.api.dtl.attributes.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Attribute extends Identifiable<Integer> {

    @SerializedName("type") AttributeType type();
    @SerializedName("name") String name();
    @SerializedName("display_name") String displayName();
    @SerializedName("merchant_count") Integer merchantCount();
    @SerializedName("partner_count") Integer partnerCount();
}
