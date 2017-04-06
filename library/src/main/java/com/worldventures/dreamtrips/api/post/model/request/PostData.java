package com.worldventures.dreamtrips.api.post.model.request;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.post.model.response.Location;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;


@Gson.TypeAdapters
@Value.Immutable
public interface PostData {

    @SerializedName("description")
    String description();
    @SerializedName("location")
    @Nullable
    Location location();
    @SerializedName("attachments")
    List<Attachment> attachments();

}
