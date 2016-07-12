package com.worldventures.dreamtrips.modules.membership.model;

import com.google.gson.annotations.SerializedName;

import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.util.Date;

public class Podcast {

    private String title;
    private String category;
    private String description;
    private Date date;
    private long size;
    private long duration;
    @SerializedName("image")
    private String imageUrl;
    @SerializedName("file")
    private String fileUrl;
    private String speaker;

    private transient CachedEntity entity;

    public Podcast() {
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public long getSize() {
        return size;
    }

    public long getDuration() {
        return duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getUid() {
        return fileUrl;
    }

    public String getSpeaker() {
        return speaker;
    }

    public CachedEntity getCacheEntity() {
        if (entity == null) {
            entity = new CachedEntity(getFileUrl(), getUid(), getTitle());
        }
        return entity;
    }

    public void setCacheEntity(CachedEntity entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "Podcast{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", size=" + size +
                ", duration=" + duration +
                ", imageUrl='" + imageUrl + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", speaker=" + speaker +
                '}';
    }
}
