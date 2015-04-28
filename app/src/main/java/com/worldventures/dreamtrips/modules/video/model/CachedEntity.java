package com.worldventures.dreamtrips.modules.video.model;

import android.content.Context;

import java.io.File;
import java.io.Serializable;

public class CachedEntity implements Serializable {

    protected static final long serialVersionUID = 12332;
    protected String url;
    protected boolean failed;
    protected int progress;
    protected String uuid;
    protected int downloadId;


    public CachedEntity(String url, String id) {
        this.url = url;
        uuid = id;
    }

    public CachedEntity() {
    }

    public boolean isFailed() {
        return failed;
    }

    public void setIsFailed(boolean isFailed) {
        this.failed = isFailed;
    }

    public boolean isCached(Context context) {
        return new File(getFilePath(context, getUrl())).exists() && getProgress() == 100;
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

    public static String getFilePath(Context context,String url) {
        return context.getFilesDir().toString() + File.separator + getFileName(url);
    }
    public static String getExternalFilePath(Context context,String url) {
        return context.getExternalCacheDir().toString() + File.separator + getFileName(url);
    }

    public String getUuid() {
        return uuid;
    }

    protected static String getFileName(String url) {
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
