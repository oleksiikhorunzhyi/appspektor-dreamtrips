package com.worldventures.dreamtrips.core.utils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class AmazonDelegate {

    private TransferUtility transferUtility;

    public AmazonDelegate(TransferUtility transferUtility) {
        this.transferUtility = transferUtility;
    }

    public TransferObserver uploadTripPhoto(ImageUploadTask imageUploadTask) {
        String filePath = imageUploadTask.getFileUri().replace("file://", "");
        File file = new File(filePath);

        String bucketName = BuildConfig.BUCKET_NAME.toLowerCase(Locale.US);
        String key = BuildConfig.BUCKET_ROOT_PATH + file.getName();
        TransferObserver transferObserver = transferUtility.upload(bucketName, key, file);
        imageUploadTask.setAmazonResultUrl("https://" + bucketName
                + ".s3.amazonaws.com/" + key);
        imageUploadTask.setAmazonTaskId(transferObserver.getId());
        return transferObserver;
    }

    public TransferObserver uploadBucketPhoto(BucketPhotoUploadTask imageUploadTask) {
        String filePath = imageUploadTask.getFilePath().replace("file://", "");
        File file = new File(filePath);

        String bucketName = BuildConfig.BUCKET_NAME.toLowerCase(Locale.US);
        String key = BuildConfig.BUCKET_ROOT_PATH + file.getName();
        TransferObserver transferObserver = transferUtility.upload(bucketName, key, file);
        imageUploadTask.setAmazonResultUrl("https://" + bucketName
                + ".s3.amazonaws.com/" + key);
        imageUploadTask.setTaskId(transferObserver.getId());
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
