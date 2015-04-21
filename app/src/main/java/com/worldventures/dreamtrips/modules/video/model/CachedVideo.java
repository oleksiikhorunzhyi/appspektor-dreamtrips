package com.worldventures.dreamtrips.modules.video.model;

import android.content.Context;

import java.io.File;
import java.io.Serializable;

public class CachedVideo implements Serializable {
    String url;
    boolean isFailed;
    int progress;
    String uuid;
    private int downloadId;




    public CachedVideo(String url, String id) {
        this.url = url;
        uuid = id;
    }

    public CachedVideo() {
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setIsFailed(boolean isFailed) {
        this.isFailed = isFailed;
    }

    public boolean isCached(Context context) {
        return new File(getFilePath(context)).exists() && getProgress() == 100;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public String getFilePath(Context context) {
        return context.getFilesDir().toString() + File.separator + getFileName(getUrl());
    }

    public String getUuid() {
        return uuid;
    }

    protected String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    @Override
    public String toString() {
        return "CachedVideo{" +
                "url='" + url + '\'' +
                ", isFailed=" + isFailed +
                ", progress=" + progress +
                ", uuid='" + uuid + '\'' +
                ", downloadId=" + downloadId +
                '}';
    }
}
