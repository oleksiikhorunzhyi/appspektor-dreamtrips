package com.worldventures.dreamtrips.api.success_stories.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Date;

@Gson.TypeAdapters
@Value.Immutable
public interface SuccessStory extends Identifiable<Integer> {
    @SerializedName("author")
    String author();
    @SerializedName("category")
    String category();
    @SerializedName("type")
    String type();
    @SerializedName("locale")
    String locale();
    @SerializedName("published_date")
    Date publishedDate();
    @SerializedName("url")
    String url();
    @SerializedName("sharing_url")
    String sharingUrl();
    @SerializedName("liked")
    boolean liked();
}
