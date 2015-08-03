package com.worldventures.dreamtrips.core.utils;

import android.content.Context;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class AmazonDelegate {

    private TransferUtility transferUtility;

    public AmazonDelegate(TransferUtility transferUtility) {
        this.transferUtility = transferUtility;
    }

    public TransferObserver uploadTripPhoto(Context context, ImageUploadTask imageUploadTask) {
        File file = UploadingFileManager.copyFileIfNeed(imageUploadTask.getFileUri(), context);
        String bucketName = BuildConfig.BUCKET_NAME.toLowerCase(Locale.US);
        String key = BuildConfig.BUCKET_ROOT_PATH + file.getName();
        TransferObserver transferObserver = transferUtility.upload(bucketName, key, file);
        imageUploadTask.setAmazonResultUrl("https://" + BuildConfig.BUCKET_NAME.toLowerCase(Locale.US)
                + ".s3.amazonaws.com/" + key);
        imageUploadTask.setAmazonTaskId(transferObserver.getId());
        return transferObserver;
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
}
