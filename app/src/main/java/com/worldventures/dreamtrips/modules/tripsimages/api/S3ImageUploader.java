package com.worldventures.dreamtrips.modules.tripsimages.api;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.modules.feed.event.PostPhotoUploadFailed;
import com.worldventures.dreamtrips.modules.feed.event.PostPhotoUploadFinished;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Locale;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class S3ImageUploader {

    @Inject
    @Global
    protected transient EventBus eventBus;
    @Inject
    protected transient AmazonS3Client amazonS3Client;
    @Inject
    protected transient Context context;
    private transient double byteTransferred;
    private transient int lastPercent;
    private ImageProgressListener imageProgressListener;

    public URL uploadImageToS3(String filePath, String taskId) throws FileNotFoundException {
        File file = UploadingFileManager.copyFileIfNeed(filePath, context);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.length());

        PutObjectRequest request = new PutObjectRequest(
                BuildConfig.BUCKET_NAME.toLowerCase(Locale.US),
                BuildConfig.BUCKET_ROOT_PATH + file.getName(),
                new FileInputStream(file), objectMetadata);

        request.setGeneralProgressListener(progressEvent -> {
            byteTransferred += progressEvent.getBytesTransferred();
            double l = byteTransferred / file.length() * 100;
            if ((l > lastPercent + 5 || l >= 95) && l <= 99) {
                lastPercent = (int) l;
                Log.v("Progress event", "send UploadProgressUpdateEvent:" + l);
                eventBus.post(new UploadProgressUpdateEvent(taskId, (int) l));
                if (imageProgressListener != null) {
                    imageProgressListener.onProgress((int) l);
                }
            }
        });

        PutObjectResult result = amazonS3Client.putObject(request);
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
                BuildConfig.BUCKET_NAME.toLowerCase(Locale.US),
                BuildConfig.BUCKET_ROOT_PATH + file.getName());
        URL pictureUrl = amazonS3Client.generatePresignedUrl(urlRequest);
        eventBus.post(new UploadProgressUpdateEvent(taskId, 100));
        if (pictureUrl != null) {
            new Handler().postDelayed(() -> eventBus.post(new PostPhotoUploadFinished(taskId,
                    pictureUrl.toString())), 300);
        } else {
            new Handler().postDelayed(() -> eventBus.post(new PostPhotoUploadFailed(taskId)), 300);
        }

        return null;
    }

    public void setProgressListener(ImageProgressListener progressListener) {
        this.imageProgressListener = progressListener;
    }

    public interface ImageProgressListener {
        void onProgress(int i);
    }
}
