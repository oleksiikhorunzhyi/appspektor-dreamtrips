package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.io.Serializable;

public class BucketPhoto extends BaseEntity implements Serializable {

    public static final long serialVersionUID = 14534647;

    @SerializedName("origin_url")
    private String originUrl;

    private String url;

    private int taskId;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbUrl() {
        String args = String.format(UniversalImageLoader.PATTERN, 256, 256);
        return String.format("%s%s", getUrl(), args);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setOriginUrl(String url) {
        this.originUrl = url;
    }

    public String getOriginUrl() {
        return originUrl;
    }
}