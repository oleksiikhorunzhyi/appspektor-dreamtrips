package com.worldventures.dreamtrips.core.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class FileUtils {

   private static final long ONE_KB = 1024;
   private static final long ONE_MB = ONE_KB * ONE_KB;
   private static final long ONE_GB = ONE_KB * ONE_MB;

   public static byte[] readByteArray(File file) throws IOException {
      byte[] buffer = new byte[(int) file.length()];
      InputStream ios = null;
      try {
         ios = new FileInputStream(file);
         if (ios.read(buffer) == -1) {
            throw new IOException("EOF reached while trying to read the whole file");
         }
      } finally {
         try {
            if (ios != null) ios.close();
         } catch (IOException ignored) {
         }
      }
      return buffer;
   }

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

   @SuppressLint("NewApi")
   @Nullable
   public static String getPath(Context context, Uri uri) throws URISyntaxException {
      final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
      String selection = null;
      String[] selectionArgs = null;
      // Uri is different in versions after KITKAT (Android 4.4), we need to
      // deal with different Uris.
      if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
         if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            return Environment.getExternalStorageDirectory() + "/" + split[1];
         } else if (isDownloadsDocument(uri)) {
            final String id = DocumentsContract.getDocumentId(uri);
            uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
         } else if (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];
            if ("image".equals(type)) {
               uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
               uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
               uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
            selection = "_id=?";
            selectionArgs = new String[]{split[1]};
         }
      }
      if ("content".equalsIgnoreCase(uri.getScheme())) {
         String[] projection = {MediaStore.Images.Media.DATA};
         Cursor cursor = null;
         try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor == null) return null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
               return cursor.getString(column_index);
            }
         } catch (Exception ignored) {
         } finally {
            if (cursor != null) cursor.close();
         }
      } else if ("file".equalsIgnoreCase(uri.getScheme())) {
         return uri.getPath();
      }

      return null;
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

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is ExternalStorageProvider.
    */
   private static boolean isExternalStorageDocument(Uri uri) {
      return "com.android.externalstorage.documents".equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is DownloadsProvider.
    */
   private static boolean isDownloadsDocument(Uri uri) {
      return "com.android.providers.downloads.documents".equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is MediaProvider.
    */
   private static boolean isMediaDocument(Uri uri) {
      return "com.android.providers.media.documents".equals(uri.getAuthority());
   }
}
