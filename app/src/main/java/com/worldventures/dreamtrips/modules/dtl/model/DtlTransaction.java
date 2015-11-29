package com.worldventures.dreamtrips.modules.dtl.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import java.util.Date;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlTransaction {

    public static final long DURATION_OF_LIFE = 4 * 60 * 60 * 1000l;

    public static final String TOKEN = "token";
    public static final String AMOUNT = "amount";
    public static final String LOCATION = "location";
    public static final String RECEIPT = "receiptPhoto";

    String checkin;
    long checkinTimestamp;
    double amount;
    double points;
    String receiptPhoto;
    String token;
    DtlTransactionLocation location;
    boolean verified;

    //

    DtlTransactionResult dtlTransactionResult;
    UploadTask uploadTask;

    public DtlTransaction() {
    }

    public DtlTransaction(long checkin, double amount, String receiptPhoto, String code) {
        this.checkinTimestamp = checkin;
        this.checkin = DateTimeUtils.convertDateToUTCString(new Date(checkin));
        this.amount = amount;
        this.receiptPhoto = receiptPhoto;
        this.token = code;
    }

    public void setLocation(DtlTransactionLocation location) {
        this.location = location;
    }

    public void setTimestamp(long timestamp) {
        this.checkinTimestamp = timestamp;
        this.checkin = DateTimeUtils.convertDateToUTCString(new Date(timestamp));
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setReceiptPhoto(String receiptPhoto) {
        this.receiptPhoto = receiptPhoto;
    }

    public void setCode(String code) {
        this.token = code;
    }

    public void setDtlTransactionResult(DtlTransactionResult dtlTransactionResult) {
        this.dtlTransactionResult = dtlTransactionResult;
    }

    public UploadTask getUploadTask() {
        return uploadTask;
    }

    public void setUploadTask(UploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public long getTimestamp() {
        return checkinTimestamp;
    }

    public boolean outOfDate(long currentTimeInMillis) {
        return currentTimeInMillis - checkinTimestamp > DURATION_OF_LIFE;
    }

    public double getAmount() {
        return amount;
    }

    public String getReceiptPhoto() {
        return receiptPhoto;
    }

    public String getCode() {
        return token;
    }

    public DtlTransactionResult getDtlTransactionResult() {
        return dtlTransactionResult;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public double getPoints() {
        return points;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isVerified() {
        return verified;
    }
}