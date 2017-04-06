package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.comment.model.Commentable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters(nullAsDefault = true)
@Value.Immutable
public abstract class TripWithDetails extends Trip implements Commentable {

    @SerializedName("content")
    public abstract List<TripContent> contentItems();

}
