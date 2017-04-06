package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.HasLanguage;
import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;
import com.worldventures.dreamtrips.api.hashtags.model.HashTagSimple;
import com.worldventures.dreamtrips.api.post.model.response.Location;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public interface Photo extends UniqueIdentifiable, HasLanguage {

    @Nullable
    @SerializedName("title")
    String title();
    @Nullable
    @SerializedName("shot_at")
    Date shotAt();

    @SerializedName("location")
    Location location();
    @SerializedName("tags")
    List<String> tags();

    @SerializedName("images")
    Image images();
    @Nullable
    @SerializedName("width")
    Integer width();
    @Nullable
    @SerializedName("height")
    Integer height();

    @SerializedName("photo_tags_count")
    int photoTagsCount();
    @SerializedName("photo_tags")
    List<PhotoTag> photoTags();

    @SerializedName("hashtags")
    List<HashTagSimple> hashtags();
}
