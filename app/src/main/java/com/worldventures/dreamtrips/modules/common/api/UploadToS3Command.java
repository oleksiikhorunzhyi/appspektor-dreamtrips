package com.worldventures.dreamtrips.modules.common.api;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.events.UploadStatusChanged;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class UploadToS3Command extends Command<String> {


    Context context;
    AmazonS3 amazonS3Client;
    EventBus eventBus;
    SnappyRepository snapper;

    UploadTask uploadTask;

    public UploadToS3Command(Context context, AmazonS3 amazonS3Client,
                             EventBus eventBus, SnappyRepository snapper, UploadTask uploadTask) {
        super(String.class);
        this.context = context;
        this.amazonS3Client = amazonS3Client;
        this.eventBus = eventBus;
        this.snapper = snapper;
        this.uploadTask = uploadTask;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        String path = getPath(context, Uri.parse(uploadTask.getFilePath()));

        if (path == null) {
            uploadTask.setStatus(UploadTask.Status.FAILED);
            saveUploadTask();
            return null;
        }

        File file = new File(path);

        String bucketName = BuildConfig.BUCKET_NAME.toLowerCase(Locale.US);
        String key = BuildConfig.BUCKET_ROOT_PATH + file.getName();

        // Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
        List<PartETag> partETags = new ArrayList<>();


        Timber.d("Preparing image upload");

        try {
            // Step 1: Initialize.
            InitiateMultipartUploadRequest initRequest = new
                    InitiateMultipartUploadRequest(bucketName, key);
            InitiateMultipartUploadResult initResponse =
                    amazonS3Client.initiateMultipartUpload(initRequest);

            uploadTask.setAmazonTaskId(initResponse.getUploadId());
            uploadTask.setBucketName(bucketName);
            uploadTask.setKey(key);
            saveUploadTask();

            long contentLength = file.length();

            long partSize = 5242880;

            Timber.d("Upload started; File length = " + contentLength);

            // Step 2: Upload parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                partSize = Math.min(partSize, (contentLength - filePosition));

                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName).withKey(key)
                        .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                // Upload part and add response to our list.
                partETags.add(amazonS3Client.uploadPart(uploadRequest).getPartETag());

                Timber.d("Uploaded part " + i);

                filePosition += partSize;
            }

            // Step 3: Complete.
            CompleteMultipartUploadRequest compRequest = new
                    CompleteMultipartUploadRequest(
                    bucketName,
                    key,
                    initResponse.getUploadId(),
                    partETags);

            CompleteMultipartUploadResult result = amazonS3Client.completeMultipartUpload(compRequest);

            Timber.d("Image uploaded");

            String url = constructResultUrl(result.getBucketName(), result.getKey());

            uploadTask.setOriginUrl(url);
            uploadTask.setStatus(UploadTask.Status.COMPLETED);
            saveUploadTask();

            Timber.d("Origin url = " + url);
            return url;
        } catch (Exception e) {
            Timber.e(e, "Upload was failed");
            uploadTask.setStatus(UploadTask.Status.FAILED);
            saveUploadTask();
        }

        return null;
    }

    private void saveUploadTask() {
        eventBus.post(new UploadStatusChanged(uploadTask));
        snapper.saveUploadTask(uploadTask);
    }

    private String constructResultUrl(String bucketName, String fileKey) {
        return "https://" + bucketName
                + ".s3.amazonaws.com/" + fileKey;
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
