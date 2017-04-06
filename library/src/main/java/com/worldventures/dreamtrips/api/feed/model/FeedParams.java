package com.worldventures.dreamtrips.api.feed.model;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.Date;

public interface FeedParams {

    @SerializedName("page_size")
    int pageSize();

    @Nullable
    @SerializedName("before_date")
    Date before();

}
