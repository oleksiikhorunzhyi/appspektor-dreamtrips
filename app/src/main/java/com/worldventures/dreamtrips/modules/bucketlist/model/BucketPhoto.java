package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.io.Serializable;

public class BucketPhoto extends BaseEntity implements Serializable {

    private String origin_url;

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

    public void setOriginUrl(String url) {
        this.origin_url = url;
    }

}
