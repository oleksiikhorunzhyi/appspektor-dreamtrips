package com.worldventures.dreamtrips.api.member_videos.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface VideoCategory {
    @SerializedName("category")
    String title();

    @SerializedName("videos")
    List<Video> videos();
}
