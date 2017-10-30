package com.worldventures.dreamtrips.modules.dtl.model.merchant.transactions;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Date;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
@Gson.TypeAdapters
public interface DetailTransactionThrst extends Serializable {
   @SerializedName("Id") @Nullable String id();
   @SerializedName("MerchantId") @Nullable String merchantId();
   @SerializedName("MerchantName") @Nullable String merchantName();
   @SerializedName("Date") @Nullable Date date();
   @SerializedName("PointsEarned")  @Nullable Double pointsEarned();
   @SerializedName("ReceiptURL")  @Nullable String receiptUrl();
   @SerializedName("RewardPointStatus")  @Nullable String rewardStatus();
   @SerializedName("SubTotal")  @Nullable Double subTotalAmount();
   @SerializedName("Total")  @Nullable Double totalAmount();
   @SerializedName("Tax")  @Nullable Double tax();
   @SerializedName("Tip")  @Nullable Double tip();
   @SerializedName("PaymentStatus")  @Nullable String paymentStatus();

}
