package com.worldventures.dreamtrips.modules.video.model;

import android.content.Context;
import android.os.Environment;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.io.File;
import java.io.Serializable;

/**
 * Note of what needs to be improved in this class:
 *
 * 1. Store progress state in actual variable, currently NOT in progress state is determined by when progress is 0,
 * which is wrong because download can be already initiated but progress is still zero
 * as connection hasn't established yet.
 * 2. This class should store link to actual cache file location as it's initiated each time with a new download
 *
 * In order to do this we should :
 * 1. Refactor related commands and presenters
 * 2. Migrate it from using default Kryo's FieldSerializer which does not support adding new fields to entities.
 */
@Deprecated
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

   public boolean inProgress() {
      return !failed && progress > 0 && progress < 100;
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
