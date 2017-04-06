package com.worldventures.dreamtrips.api.member_videos.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.HasLanguage;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface Video extends HasLanguage {

    @SerializedName("image_url")
    String imageUrl();

    @SerializedName("video_url")
    String videoUrl();

    @SerializedName("name")
    String name();

    @Nullable
    @SerializedName("category")
    String category();

    @Nullable
    @SerializedName("duration")
    String duration();

    @SerializedName("sort_order")
    int sortOrder();
}
