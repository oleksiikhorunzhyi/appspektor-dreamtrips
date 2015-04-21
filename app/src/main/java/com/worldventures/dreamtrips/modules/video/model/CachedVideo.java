package com.worldventures.dreamtrips.modules.video.model;

import android.content.Context;

import java.io.File;
import java.io.Serializable;

public class CachedVideo implements Serializable {

    protected static final long serialVersionUID = 12332;
    protected String url;
    protected boolean failed;
    protected int progress;
    protected String uuid;
    protected int downloadId;


    public CachedVideo(String url, String id) {
        this.url = url;
        uuid = id;
    }

    public CachedVideo() {
    }

    public boolean isFailed() {
        return failed;
    }

    public void setIsFailed(boolean isFailed) {
        this.failed = isFailed;
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
                ", failed=" + failed +
                ", progress=" + progress +
                ", uuid='" + uuid + '\'' +
                ", downloadId=" + downloadId +
                '}';
    }
}
