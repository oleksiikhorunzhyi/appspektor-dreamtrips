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

   @SerializedName("MerchantName") @Nullable String merchantName();
   @SerializedName("Date") @Nullable Date date();
   @SerializedName("PointsEarned")  @Nullable Integer pointsEarned();
   @SerializedName("ReceiptUrl")  @Nullable String receiptUrl();
   @SerializedName("RewardPointStatus")  @Nullable String rewardStatus();
   @SerializedName("SubTotal")  @Nullable String subTotalAmount();
   @SerializedName("TotalAmount")  @Nullable String totalAmount();
   @SerializedName("Tax")  @Nullable String tax();
   @SerializedName("Tip")  @Nullable String tip();

}
