package com.worldventures.dreamtrips.api.invitation.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.photos.model.Image;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface InvitationTemplate extends Identifiable<Integer> {

    @SerializedName("type")
    InvitationType type();

    @SerializedName("title")
    String title();

    @SerializedName("category")
    String category();

    @SerializedName("content")
    String content();

    @SerializedName("cover_image")
    Image coverImage();

    @Nullable
    @SerializedName("video")
    String video();

    @SerializedName("locale")
    String locale();
}
