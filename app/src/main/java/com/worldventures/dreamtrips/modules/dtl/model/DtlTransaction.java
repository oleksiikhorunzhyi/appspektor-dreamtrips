package com.worldventures.dreamtrips.modules.dtl.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import java.util.Date;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlTransaction {

    public static final long DURATION_OF_LIFE = 4 * 60 * 60 * 1000l;

    public static final String MERCHANT_TOKEN = "merchant_token";
    public static final String BILL_TOTAL = "bill_total";
    public static final String LOCATION = "location.ll";
    public static final String RECEIPT_PHOTO_URL = "receipt_photo_url";

    String checkinTime;
    long checkinTimestamp;
    double billTotal;
    double points;
    String receiptPhotoUrl;
    String merchantToken;
    DtlTransactionLocation location;
    boolean verified;

    //

    DtlTransactionResult dtlTransactionResult;
    UploadTask uploadTask;

    public DtlTransaction() {
    }

    public DtlTransaction(long checkinTime, double billTotal, String receiptPhotoUrl, String code) {
        this.checkinTimestamp = checkinTime;
        this.checkinTime = DateTimeUtils.convertDateToUTCString(new Date(checkinTime));
        this.billTotal = billTotal;
        this.receiptPhotoUrl = receiptPhotoUrl;
        this.merchantToken = code;
    }

    public void setLocation(DtlTransactionLocation location) {
        this.location = location;
    }

    public void setTimestamp(long timestamp) {
        this.checkinTimestamp = timestamp;
        this.checkinTime = DateTimeUtils.convertDateToUTCString(new Date(timestamp));
    }

    public void setBillTotal(double billTotal) {
        this.billTotal = billTotal;
    }

    public void setReceiptPhotoUrl(String receiptPhotoUrl) {
        this.receiptPhotoUrl = receiptPhotoUrl;
    }

    public void setCode(String code) {
        this.merchantToken = code;
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

    public double getBillTotal() {
        return billTotal;
    }

    public String getReceiptPhotoUrl() {
        return receiptPhotoUrl;
    }

    public String getCode() {
        return merchantToken;
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