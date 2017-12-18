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
public abstract class DetailTransactionThrst implements Serializable {
   @SerializedName("Id")
   @Nullable
   public abstract String id();
   @SerializedName("MerchantId")
   @Nullable
   public abstract String merchantId();
   @SerializedName("MerchantName")
   @Nullable
   public abstract String merchantName();
   @SerializedName("Date")
   @Nullable
   public abstract Date date();
   @SerializedName("PointsEarned")
   @Nullable
   public abstract Double pointsEarned();
   @SerializedName("ReceiptURL")
   @Nullable
   public abstract String receiptUrl();
   @SerializedName("RewardPointStatus")
   @Nullable
   public abstract String rewardStatus();
   @SerializedName("SubTotal")
   @Nullable
   public abstract Double subTotalAmount();
   @SerializedName("Total")
   @Nullable
   public abstract Double totalAmount();
   @SerializedName("Tax")
   @Nullable
   public abstract Double tax();
   @SerializedName("Tip")
   @Nullable
   public abstract Double tip();
   @SerializedName("CurrencySymbol")
   public abstract String currencySymbol();
   @SerializedName("CurrencyCode")
   public abstract String currencyCode();

   @SerializedName("PaymentStatus")
   @Nullable
   @Value.Default
   public PaymentStatus paymentStatus() {
      return PaymentStatus.UNKNOWN;
   }

   @SerializedName("IsThrstTransaction")
   @Nullable
   public abstract Boolean isThrstTransaction();

   public enum PaymentStatus {
      @SerializedName("INITIATED")INITIATED,
      @SerializedName("SUCCESSFUL")SUCCESSFUL,
      @SerializedName("REFUNDED")REFUNDED,
      UNKNOWN
   }
}
