package com.worldventures.dreamtrips.api.member_videos.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface VideoLanguage {
    @SerializedName("title")
    String title();

    @SerializedName("locale_name")
    String localeName();
}
