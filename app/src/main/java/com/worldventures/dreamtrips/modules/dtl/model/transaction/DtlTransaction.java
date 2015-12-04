package com.worldventures.dreamtrips.modules.dtl.model.transaction;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import java.util.Date;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlTransaction {

    public static final long DURATION_OF_LIFE = 4 * 60 * 60 * 1000l;

    public static final String BILL_TOTAL = "bill_total";
    public static final String LOCATION = "location.ll";
    public static final String CHECKIN = "checkin_time";
    public static final String MERCHANT_TOKEN = "merchant_token";
    public static final String RECEIPT_PHOTO_URL = "receipt_photo_url";

    long checkinTimestamp;
    double billTotal;
    double points;
    String receiptPhotoUrl;
    String merchantToken;
    double lat;
    double lng;
    boolean verified;
    DtlTransactionResult dtlTransactionResult;
    UploadTask uploadTask;

    public DtlTransaction() {
    }

    public DtlTransaction(long checkinTime, double billTotal, String receiptPhotoUrl, String code) {
        this.checkinTimestamp = checkinTime;
        this.billTotal = billTotal;
        this.receiptPhotoUrl = receiptPhotoUrl;
        this.merchantToken = code;
    }

    public void setTimestamp(long timestamp) {
        this.checkinTimestamp = timestamp;
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

    public boolean outOfDate(long currentTimeInMillis) {
        return currentTimeInMillis - checkinTimestamp > DURATION_OF_LIFE ||
                lat == 0.0d || lng == 0.0d;
    }

    public double getBillTotal() {
        return billTotal;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
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

    public Request asTransactionRequest() {
        Request dtlTransactionRequest = new Request();
        dtlTransactionRequest.billTotal = billTotal;
        dtlTransactionRequest.checkinTime = DateTimeUtils.convertDateToUTCString(new Date(checkinTimestamp));
        dtlTransactionRequest.points = points;
        dtlTransactionRequest.receiptPhotoUrl = receiptPhotoUrl;
        dtlTransactionRequest.merchantToken = merchantToken;
        dtlTransactionRequest.location = DtlTransactionLocation.fromLatLng(lat, lng);
        return dtlTransactionRequest;
    }

    public static class Request {
        String checkinTime;
        double billTotal;
        double points;
        String receiptPhotoUrl;
        String merchantToken;
        DtlTransactionLocation location;
    }
}
