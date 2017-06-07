package com.worldventures.dreamtrips.core.utils;

import android.content.Context;

public class FilePathProvider {

   private final String filesDir;
   private final String externalCacheDir;

   public FilePathProvider(Context context) {
      filesDir = context.getFilesDir().getPath();
      externalCacheDir = context.getExternalCacheDir().getPath();
   }

   public String getFilesDir() {
      return filesDir;
   }

   public String getExternalCacheDir() {
      return externalCacheDir;
   }
}
