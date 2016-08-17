package com.worldventures.dreamtrips.modules.video.model;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.Serializable;

public class CachedEntity implements Serializable {

   protected static final long serialVersionUID = 12332;
   protected String url;
   protected boolean failed;
   protected int progress;
   protected String uuid;
   protected int downloadId;
   protected String name;

   public CachedEntity(String url, String id, String name) {
      this.url = url;
      uuid = id;
      this.name = name;
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

   public boolean isCached(String type) {
      return new File(getFileForStorage(type, getUrl())).exists() && getProgress() == 100;
   }

   public String getName() {
      return name;
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

   public static String getFilePath(Context context, String url) {
      return context.getFilesDir().getPath() + File.separator + getFileName(url);
   }

   public static String getExternalFilePath(Context context, String url) {
      return context.getExternalCacheDir().getPath() + File.separator + getFileName(url);
   }

   public static String getFileForStorage(String type, String url) {
      File podcastsPath = Environment.getExternalStoragePublicDirectory(type);
      podcastsPath.mkdirs();
      return podcastsPath + File.separator + getFileName(url);
   }

   public String getUuid() {
      return uuid;
   }

   public static String getFileName(String url) {
      return url.substring(url.lastIndexOf("/") + 1);
   }

   @Override
   public String toString() {
      return "CachedEntity{" +
            "url='" + url + '\'' +
            ", failed=" + failed +
            ", create=" + progress +
            ", uuid='" + uuid + '\'' +
            ", downloadId=" + downloadId +
            '}';
   }
}
