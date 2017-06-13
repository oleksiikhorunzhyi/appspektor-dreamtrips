package com.worldventures.dreamtrips.core.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.innahema.collections.query.queriables.Queryable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class FileUtils {

   private static final long ONE_KB = 1024;
   private static final long ONE_MB = ONE_KB * ONE_KB;
   private static final long ONE_GB = ONE_KB * ONE_MB;

   public static long getFileSize(String path) {
      return new File(path).length();
   }

   public static void cleanDirectory(Context context, File directory, List<String> exceptFilePaths) throws IOException {
      if (!directory.exists()) {
         throw new IllegalArgumentException(directory + " does not exist");
      }

      if (!directory.isDirectory()) {
         throw new IllegalArgumentException(directory + " is not a directory");
      }

      File[] files = directory.listFiles();
      if (files == null) {  // null if security restricted
         throw new IOException("Failed to list contents of " + directory);
      }

      Queryable.from(files).filter(file -> !isExcepted(file.getAbsolutePath(), exceptFilePaths)).forEachR(file -> {
         try {
            forceDelete(context, file, exceptFilePaths);
         } catch (IOException e) {
            Timber.e("Unable to delete file: " + file, e);
         }
      });

   }

   private static boolean isExcepted(String filePath, List<String> exceptedFilePaths) {
      for (String exceptFilePath : exceptedFilePaths) {
         //exceptedFilePath contains "file://" and unlike filePath
         if (exceptFilePath.endsWith(filePath)) return true;
      }
      return false;
   }

   public static void forceDelete(Context context, File file, List<String> exceptFilePaths) throws IOException {
      if (file.isDirectory()) {
         cleanDirectory(context, file, exceptFilePaths);
      } else {
         if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + file);
         }

         if (!file.delete()) {
            String message = "Unable to delete file: " + file;
            throw new IOException(message);
         }

         // Set up the projection (we only need the ID)
         String[] projection = {MediaStore.Images.Media._ID};

         // Match on the file path
         String selection = MediaStore.Images.Media.DATA + " = ?";
         String[] selectionArgs = new String[]{file.getAbsolutePath()};

         // Query for the ID of the media matching the file path
         Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
         ContentResolver contentResolver = context.getContentResolver();
         Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
         if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
         }
         c.close();
      }
   }

   public static String buildFilePathOriginal(String foldername, String extension) {
      return getDirectory(foldername) + File.separator + Calendar.getInstance().getTimeInMillis() + "." + extension;

   }

   public static String getDirectory(String foldername) {
      File directory;
      directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + foldername);
      if (!directory.exists()) directory.mkdirs();

      return directory.getAbsolutePath();
   }

   public static String byteCountToDisplaySize(long size) {
      String displaySize;

      if (size / ONE_GB > 0) displaySize = String.valueOf(size / ONE_GB) + " GB";
      else if (size / ONE_MB > 0) displaySize = String.valueOf(size / ONE_MB) + " MB";
      else if (size / ONE_KB > 0) displaySize = String.valueOf(size / ONE_KB) + " KB";
      else displaySize = String.valueOf(size) + " bytes";

      return displaySize;
   }


}
