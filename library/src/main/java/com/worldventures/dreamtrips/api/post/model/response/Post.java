package com.worldventures.dreamtrips.api.post.model.response;


import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.HasLanguage;
import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;
import com.worldventures.dreamtrips.api.entity.model.EntityHolder;
import com.worldventures.dreamtrips.api.hashtags.model.HashTagSimple;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public interface Post extends UniqueIdentifiable, HasLanguage {

    @SerializedName("description")
    String description();
    @SerializedName("user")
    ShortUserProfile owner();
    @SerializedName("attachments")
    List<EntityHolder> attachments();
    @SerializedName("hashtags")
    List<HashTagSimple> hashtags();
    @SerializedName("location")
    @Nullable
    Location location();
    @SerializedName("created_at")
    Date createdAt();
    @SerializedName("updated_at")
    Date updatedAt();

}
