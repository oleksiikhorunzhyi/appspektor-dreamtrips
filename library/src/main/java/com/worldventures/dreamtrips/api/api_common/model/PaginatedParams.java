package com.worldventures.dreamtrips.api.api_common.model;

import com.google.gson.annotations.SerializedName;

public interface PaginatedParams {

    @SerializedName("page")
    int page();

    @SerializedName("per_page")
    int perPage();
}
