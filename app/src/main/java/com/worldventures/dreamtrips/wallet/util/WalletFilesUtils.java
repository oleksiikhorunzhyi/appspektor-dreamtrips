package com.worldventures.dreamtrips.wallet.util;


import android.content.Context;
import android.net.Uri;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;

import java.io.File;

public class WalletFilesUtils {

   public static final String FIRMWARE_FILE_NAME = "flyee_firmware.zip";

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

   public static File getAppropriateFirmwareFile(Context context) {
      File mostAppropriateStorage = getMostAppropriateCacheStorage(context);
      return new File(getPathForFirmware(mostAppropriateStorage));
   }

   private static String getPathForFirmware(File filePath) {
      return filePath.getAbsolutePath() + File.separator + FIRMWARE_FILE_NAME;
   }

   public static void checkStorageAvailability(Context context, long requiredStorageInBytes) throws NotEnoughSpaceException {
      File mostAppropriateStorage = getMostAppropriateCacheStorage(context);
      long availableBytes = getAvailableBytes(mostAppropriateStorage);
      boolean enoughSpace = availableBytes > requiredStorageInBytes;
      if (!enoughSpace) {
         throw new NotEnoughSpaceException(requiredStorageInBytes - availableBytes);
      }
   }

   public static long getAvailableBytes(File directory) {
      return new StatFs(directory.getPath()).getAvailableBytes();
   }

   public static class NotEnoughSpaceException extends Throwable {
      private final long missingByteSpace;

      public NotEnoughSpaceException(long missingByteSpace) {this.missingByteSpace = missingByteSpace;}

      public long getMissingByteSpace() {
         return missingByteSpace;
      }
   }

   public static File parseUriToFile(String strUri) {
      final Uri uri = Uri.parse(strUri);
      if (uri.getScheme() == null) {
         throw new IllegalArgumentException("Uri scheme is missed: " + strUri);
      }
      final File file = new File(uri.getPath());
      if (file.isFile()) {
         return file;
      } else {
         throw new IllegalArgumentException(strUri + " doesn't correspond to File path scheme");
      }
   }

   public static Uri parsePathToUri(String path) {
      final Uri uri = Uri.parse(path);
      if (uri.getScheme() == null) {
         return Uri.fromFile(new File(path));
      }
      return Uri.parse(path);
   }
}
