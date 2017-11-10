package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

public class ThrstFlowBundle implements Parcelable {

   private final Merchant merchant;
   private final String receiptUrl;
   private final String token;
   private final String transactionId;

   public ThrstFlowBundle(Merchant merchant, String receiptUrl, String token, String transactionId) {
      this.merchant = merchant;
      this.receiptUrl = receiptUrl;
      this.token = token;
      this.transactionId = transactionId;
   }

   public Merchant getMerchant() {
      return merchant;
   }

   public String getReceiptUrl() {
      return receiptUrl;
   }

   public String getToken() {
      return token;
   }

   public String getTransactionId() {
      return transactionId;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable part
   ///////////////////////////////////////////////////////////////////////////

   protected ThrstFlowBundle(Parcel in) {
      merchant = (Merchant) in.readSerializable();
      receiptUrl = in.readString();
      token = in.readString();
      transactionId = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(merchant);
      dest.writeString(receiptUrl);
      dest.writeString(token);
      dest.writeString(transactionId);
   }

   public static final Creator<ThrstFlowBundle> CREATOR = new Creator<ThrstFlowBundle>() {
      @Override
      public ThrstFlowBundle createFromParcel(Parcel in) {
         return new ThrstFlowBundle(in);
      }

      @Override
      public ThrstFlowBundle[] newArray(int size) {
         return new ThrstFlowBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }
}
