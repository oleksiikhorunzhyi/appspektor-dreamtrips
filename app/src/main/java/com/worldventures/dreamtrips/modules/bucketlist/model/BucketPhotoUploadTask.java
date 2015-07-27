package com.worldventures.dreamtrips.modules.bucketlist.model;

import java.io.Serializable;

public class BucketPhotoUploadTask implements Serializable {

    private String filePath;
    private long taskId;
    private int progress;
    private int bucketId;
    private boolean failed;
    private String type;

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setSelectionType(String type) {
        this.type = type;
    }

    public String getSelectionType() {
        return type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public int getBucketId() {
        return bucketId;
    }

    public void setBucketId(int bucketId) {
        this.bucketId = bucketId;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public boolean isFailed() {
        return failed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BucketPhotoUploadTask that = (BucketPhotoUploadTask) o;

        return taskId == that.taskId;

    }

    @Override
    public int hashCode() {
        return (int) taskId;
    }
}
