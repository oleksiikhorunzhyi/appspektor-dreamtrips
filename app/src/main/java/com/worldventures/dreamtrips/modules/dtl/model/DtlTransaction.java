package com.worldventures.dreamtrips.modules.dtl.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlTransaction {

    public static final long DURATION_OF_LIFE = 4 * 60 * 60 * 1000l;

    long checkin;
    double amount;
    String receiptPhoto;
    String code;
    DtlTransactionResult dtlTransactionResult;

    public DtlTransaction() {
    }

    public DtlTransaction(long checkin, double amount, String receiptPhoto, String code) {
        this.checkin = checkin;
        this.amount = amount;
        this.receiptPhoto = receiptPhoto;
        this.code = code;
    }

    public void setTimestamp(long timestamp) {
        this.checkin = timestamp;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setReceiptPhoto(String receiptPhoto) {
        this.receiptPhoto = receiptPhoto;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDtlTransactionResult(DtlTransactionResult dtlTransactionResult) {
        this.dtlTransactionResult = dtlTransactionResult;
    }

    public long getTimestamp() {
        return checkin;
    }

    public boolean outOfDate(long currentTimeInMillis) {
        return currentTimeInMillis - checkin > DURATION_OF_LIFE;
    }

    public double getAmount() {
        return amount;
    }

    public String getReceiptPhoto() {
        return receiptPhoto;
    }

    public String getCode() {
        return code;
    }

    public DtlTransactionResult getDtlTransactionResult() {
        return dtlTransactionResult;
    }
}
