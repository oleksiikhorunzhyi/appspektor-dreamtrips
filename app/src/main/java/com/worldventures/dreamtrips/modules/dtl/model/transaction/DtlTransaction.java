package com.worldventures.dreamtrips.modules.dtl.model.transaction;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import org.immutables.value.Value;

import java.util.Calendar;
import java.util.Date;

@Value.Immutable
@Value.Style(privateNoargConstructor = true, defaultAsDefault = true)
@DefaultSerializer(CompatibleFieldSerializer.class)
public abstract class DtlTransaction {

    public static final long DURATION_OF_LIFE = 4 * 60 * 60 * 1000L; // 4 hours

    public static final String BILL_TOTAL = "bill_total";
    public static final String LOCATION = "location.ll";
    public static final String CHECKIN = "checkin_time";
    public static final String MERCHANT_TOKEN = "merchant_token";
    public static final String RECEIPT_PHOTO_URL = "receipt_photo_url";

    @Value.Default
    public long getCheckinTimestamp() {
        return Calendar.getInstance().getTimeInMillis();
    };

    @Value.Default
    public double getBillTotal() {
        return 0d;
    };

    @Value.Default
    public double getPoints() {
        return 0d;
    };

    @Nullable
    public abstract String getReceiptPhotoUrl();

    @Nullable
    public abstract String getMerchantToken();

    public abstract double getLat();

    public abstract double getLng();

    @Value.Default
    public boolean isVerified() {
        return false;
    };

    @Nullable
    public abstract DtlTransactionResult getDtlTransactionResult();

    @Nullable
    public abstract UploadTask getUploadTask();

    @Value.Derived
    public boolean isOutOfDate(long currentTimeInMillis) {
        return currentTimeInMillis - getCheckinTimestamp() > DURATION_OF_LIFE ||
                getLat() == 0.0d || getLng() == 0.0d;
    }

    @Value.Derived
    public boolean isMerchantCodeScanned() {
        return !TextUtils.isEmpty(getMerchantToken());
    }

    @Value.Derived
    public boolean isReceiptPhotoUploaded() {
        return !TextUtils.isEmpty(getReceiptPhotoUrl());
    }

    @Value.Derived @Value.Lazy
    public Request asTransactionRequest(String currencyCode) {
        Request request = new Request();
        request.billTotal = getBillTotal();
        request.checkinTime = DateTimeUtils.convertDateToUTCString(new Date(getCheckinTimestamp()));
        request.receiptPhotoUrl = getReceiptPhotoUrl();
        request.merchantToken = getMerchantToken();
        request.location = DtlTransactionLocation.fromLatLng(getLat(), getLng());
        request.currencyCode = currencyCode;
        return request;
    }

    public static class Request {
        String checkinTime;
        double billTotal;
        String receiptPhotoUrl;
        String merchantToken;
        DtlTransactionLocation location;
        String currencyCode;
    }
}
