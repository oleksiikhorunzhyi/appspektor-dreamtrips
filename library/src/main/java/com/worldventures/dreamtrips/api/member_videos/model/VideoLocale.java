package com.worldventures.dreamtrips.api.member_videos.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface VideoLocale {
    @SerializedName("title")
    String title();

    @SerializedName("country")
    String country();

    @SerializedName("icon")
    String iconUrl();

    @SerializedName("language")
    List<VideoLanguage> languages();
}
