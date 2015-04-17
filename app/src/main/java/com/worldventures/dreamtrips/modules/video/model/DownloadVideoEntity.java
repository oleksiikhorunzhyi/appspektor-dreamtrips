package com.worldventures.dreamtrips.modules.video.model;

import android.content.Context;

import com.worldventures.dreamtrips.modules.infopages.model.Video;

import java.io.File;
import java.io.Serializable;

public class DownloadVideoEntity implements Serializable {
    String url;
    String id;
    boolean isFailed;
    int progress;

    public DownloadVideoEntity(Video video) {
        url = video.getMp4Url();
        id = String.valueOf(video.getId());
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setIsFailed(boolean isFailed) {
        this.isFailed = isFailed;
    }

    public boolean isCached(Context context) {
        return new File(getFilePath(context)).exists();
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

    public String getId() {
        return id;
    }

    protected String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
