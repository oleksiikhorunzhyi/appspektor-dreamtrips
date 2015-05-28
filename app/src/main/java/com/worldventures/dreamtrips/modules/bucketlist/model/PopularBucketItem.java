package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

public class PopularBucketItem extends BaseEntity implements Filterable {

    private String name;
    private boolean liked;
    @SerializedName("likes_count")
    private int likesCount;
    private String description;
    @SerializedName("short_description")
    private String shortDescription;
    @SerializedName("cover_photo")
    private BucketPhoto coverPhoto;
    private transient String type;
    private transient boolean loading = false;

    public String getCoverPhotoUrl(int w, int h) {
        return coverPhoto != null ? coverPhoto.getFSImage().getUrl(w, h) : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikes_count() {
        return likesCount;
    }

    public void setLikes_count(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShort_description() {
        return shortDescription;
    }

    public void setShort_description(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public boolean containsQuery(String query) {
        return query == null || name.toLowerCase().contains(query)
                || description.toLowerCase().contains(query);
    }
}