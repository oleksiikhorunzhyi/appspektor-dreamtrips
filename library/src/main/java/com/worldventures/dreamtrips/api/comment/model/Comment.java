package com.worldventures.dreamtrips.api.comment.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.HasLanguage;
import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;


@Gson.TypeAdapters
@Value.Immutable
public interface Comment extends UniqueIdentifiable, HasLanguage {

    @SerializedName("origin_id")
    String postId(); // commented item uid
    @SerializedName("text")
    String text();
    @SerializedName("user")
    ShortUserProfile author();
    @SerializedName("parent_id")
    @Nullable
    String parentId();
    @SerializedName("created_at")
    Date createdTime();
    @SerializedName("updated_at")
    Date updatedTime();
    @SerializedName("company")
    @Nullable
    String company();

}
