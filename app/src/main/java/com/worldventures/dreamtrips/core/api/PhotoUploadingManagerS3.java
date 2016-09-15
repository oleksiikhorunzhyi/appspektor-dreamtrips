package com.worldventures.dreamtrips.core.api;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class PhotoUploadingManagerS3 {

   @Inject Context context;
   @Inject TransferUtility transferUtility;

   public PhotoUploadingManagerS3(Injector injector) {
      injector.inject(this);
   }

   public void cancelUploading(UploadTask uploadTask) {
      transferUtility.cancel(Integer.valueOf(uploadTask.getAmazonTaskId()));
   }

   public List<TransferObserver> getUploadingTranferListeners() {
      return transferUtility.getTransfersWithType(TransferType.UPLOAD);
   }

   public TransferObserver getTransferById(String id) {
      return transferUtility.getTransferById(Integer.valueOf(id));
   }

   public TransferObserver getTransferById(int id) {
      return transferUtility.getTransferById(id);
   }

   public TransferObserver upload(UploadTask uploadTask) {
      String path = null;
      try {
         path = getPath(context, Uri.parse(uploadTask.getFilePath()));
      } catch (URISyntaxException e) {
         e.printStackTrace();
      }

      if (path == null) return null;

      File file = new File(path);
      String bucketName = BuildConfig.BUCKET_NAME.toLowerCase(Locale.US);
      String key = BuildConfig.BUCKET_ROOT_PATH + file.getName();

      uploadTask.setBucketName(bucketName);
      uploadTask.setKey(key);

      return transferUtility.upload(bucketName, key, file);
   }

   public String getResultUrl(UploadTask uploadTask) {
      return uploadTask == null ? null : "https://" + uploadTask.getBucketName() + ".s3.amazonaws.com/" + uploadTask.getKey();
   }

   public String getResultUrl(String filePath) {
      String bucketName = BuildConfig.BUCKET_NAME.toLowerCase(Locale.US);
      String key = BuildConfig.BUCKET_ROOT_PATH + new File(filePath).getName();
      return "https://" + bucketName + ".s3.amazonaws.com/" + key;
   }


   @SuppressLint("NewApi")
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
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
               return cursor.getString(column_index);
            }
         } catch (Exception e) {
         }
      } else if ("file".equalsIgnoreCase(uri.getScheme())) {
         return uri.getPath();
      }
      return null;
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is ExternalStorageProvider.
    */
   public static boolean isExternalStorageDocument(Uri uri) {
      return "com.android.externalstorage.documents".equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is DownloadsProvider.
    */
   public static boolean isDownloadsDocument(Uri uri) {
      return "com.android.providers.downloads.documents".equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is MediaProvider.
    */
   public static boolean isMediaDocument(Uri uri) {
      return "com.android.providers.media.documents".equals(uri.getAuthority());
   }

}
