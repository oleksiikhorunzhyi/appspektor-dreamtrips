package com.worldventures.dreamtrips.core.utils;

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
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

public class AmazonDelegate {

    private TransferUtility transferUtility;

    public AmazonDelegate(TransferUtility transferUtility) {
        this.transferUtility = transferUtility;
    }

    public TransferObserver uploadTripPhoto(Context context, ImageUploadTask imageUploadTask)
            throws URISyntaxException, IllegalArgumentException {
        String filePath = getPath(context, Uri.parse(imageUploadTask.getFileUri()));

        if (filePath != null) {
            File file = new File(filePath);

            String bucketName = BuildConfig.BUCKET_NAME.toLowerCase(Locale.US);
            String key = BuildConfig.BUCKET_ROOT_PATH + file.getName();
            TransferObserver transferObserver = transferUtility.upload(bucketName, key, file);
            imageUploadTask.setAmazonResultUrl("https://" + bucketName
                    + ".s3.amazonaws.com/" + key);
            imageUploadTask.setAmazonTaskId(transferObserver.getId());
            return transferObserver;
        } else {
            throw new IllegalArgumentException("Path should not be null");
        }
    }

    public TransferObserver uploadBucketPhoto(Context context, BucketPhotoUploadTask imageUploadTask)
            throws URISyntaxException, IllegalArgumentException {
        String path = getPath(context, Uri.parse(imageUploadTask.getFilePath()));

        if (path != null) {
            File file = new File(path);

            String bucketName = BuildConfig.BUCKET_NAME.toLowerCase(Locale.US);
            String key = BuildConfig.BUCKET_ROOT_PATH + file.getName();
            TransferObserver transferObserver = transferUtility.upload(bucketName, key, file);
            imageUploadTask.setAmazonResultUrl("https://" + bucketName
                    + ".s3.amazonaws.com/" + key);
            imageUploadTask.setTaskId(transferObserver.getId());
            return transferObserver;
        } else {
            throw new IllegalArgumentException("Path should not be null");
        }
    }


    public void cancel(int id) {
        transferUtility.cancel(id);
    }

    public TransferObserver getTransferById(int id) {
        return transferUtility.getTransferById(id);
    }

    public List<TransferObserver> getUploadingTransfers() {
        return transferUtility.getTransfersWithType(TransferType.UPLOAD);
    }

    @SuppressLint("NewApi")
    private String getPath(Context context, Uri uri) throws URISyntaxException {
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
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
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
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
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
