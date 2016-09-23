package com.worldventures.dreamtrips.wallet.util;


import android.content.Context;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;

import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;

import java.io.File;

public class WalletFilesUtils {

   private WalletFilesUtils() {}

   public static File getMostAppropriateCacheStorage(Context context) {
      File result = null;
      long resultAvailableSizeInBytes = 0;
      File[] externalStorageFiles = ContextCompat.getExternalCacheDirs(context);
      for (File file : externalStorageFiles) {
         if (file != null) {
            long availableSizeInBytes = new StatFs(file.getPath()).getAvailableBytes();
            if (result == null || resultAvailableSizeInBytes < availableSizeInBytes) {
               result = file;
               resultAvailableSizeInBytes = availableSizeInBytes;
            }
         }
      }
      return result;
   }

   public static long getAvailableBytes(File directory, TemporaryStorage temporaryStorage) {
      long availableBytes = new StatFs(directory.getPath()).getAvailableBytes();
      if (!temporaryStorage.enoughSpaceForFirmware()) availableBytes = 0;
      return availableBytes;
   }

}
