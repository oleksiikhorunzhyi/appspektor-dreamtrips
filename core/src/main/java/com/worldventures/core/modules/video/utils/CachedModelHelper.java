package com.worldventures.core.modules.video.utils;


import android.os.Environment;

import com.worldventures.core.model.CachedModel;
import com.worldventures.core.service.FilePathProvider;

import java.io.File;

public class CachedModelHelper {

   private final FilePathProvider filePathProvider;

   public CachedModelHelper(FilePathProvider filePathProvider) {
      this.filePathProvider = filePathProvider;
   }

   public boolean isCached(CachedModel cachedModel) {
      return new File(getFilePath(cachedModel.getUrl())).exists() && cachedModel.getProgress() == 100;
   }

   public boolean isCachedPodcast(CachedModel cachedModel) {
      return new File(getFileForStorage(Environment.DIRECTORY_PODCASTS, cachedModel.getUrl())).exists() && cachedModel.getProgress() == 100;
   }

   public String getFilePath(String url) {
      return filePathProvider.getFilesDir() + File.separator + getFileName(url);
   }

   public String getExternalFilePath(String url) {
      return filePathProvider.getExternalCacheDir() + File.separator + getFileName(url);
   }

   public String getFileForStorage(String type, String url) {
      File podcastsPath = Environment.getExternalStoragePublicDirectory(type);
      podcastsPath.mkdirs();
      return podcastsPath + File.separator + getFileName(url);
   }

   public String getFileName(String url) {
      return url.substring(url.lastIndexOf('/') + 1);
   }

}
