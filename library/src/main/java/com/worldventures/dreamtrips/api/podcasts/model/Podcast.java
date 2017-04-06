package com.worldventures.dreamtrips.api.podcasts.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Date;

@Gson.TypeAdapters(nullAsDefault = true)
@Value.Immutable
public abstract class Podcast {

    @SerializedName("title")
    public abstract String title();

    @SerializedName("description")
    public abstract String description();

    @SerializedName("speaker")
    public abstract String speaker();

    @SerializedName("category")
    public abstract String category();

    @SerializedName("date")
    public abstract Date date();

    @SerializedName("file")
    public abstract String audioURL();

    @SerializedName("image")
    public abstract String imageURL();

    @Value.Default
    @SerializedName("size")
    public int size() {
        return 0;
    }

    @SerializedName("duration")
    public abstract int duration();
}
