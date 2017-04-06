package com.worldventures.dreamtrips.api.api_common.model;

import com.google.gson.annotations.SerializedName;

public interface UniqueIdentifiable {
    @SerializedName("uid")
    String uid();
}
