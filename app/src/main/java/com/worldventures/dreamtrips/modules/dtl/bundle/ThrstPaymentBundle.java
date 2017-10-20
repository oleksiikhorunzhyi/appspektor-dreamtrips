package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

public class ThrstPaymentBundle implements Parcelable {

   private final Merchant merchant;
   private final boolean isPaid;
   private final String totalAmount;
   private final String earnedPoints;
   private final String totalPoints;
   private final String receiptURL;

   public ThrstPaymentBundle(Merchant merchant, boolean isPaid, String totalAmount, String earnedPoints, String totalPoints, String receiptURL) {
      this.merchant = merchant;
      this.isPaid = isPaid;
      this.totalAmount = totalAmount;
      this.earnedPoints = earnedPoints;
      this.totalPoints = totalPoints;
      this.receiptURL = receiptURL;
   }

   public Merchant getMerchant() {
      return merchant;
   }

   public boolean isPaid() {
      return isPaid;
   }

   public String getTotalAmount() {
      return totalAmount;
   }

   public String getEarnedPoints() {
      return earnedPoints;
   }

   public String getTotalPoints() {
      return totalPoints;
   }

   public String getReceiptURL() {
      return receiptURL;
   }
   ///////////////////////////////////////////////////////////////////////////
   // Parcelable part
   ///////////////////////////////////////////////////////////////////////////

   protected ThrstPaymentBundle(Parcel in) {
      merchant = (Merchant) in.readSerializable();
      isPaid = in.readByte() != 0;
      totalAmount = in.readString();
      earnedPoints = in.readString();
      totalPoints = in.readString();
      receiptURL = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(merchant);
      dest.writeByte((byte) (isPaid ? 1 : 0));
      dest.writeString(totalAmount);
      dest.writeString(earnedPoints);
      dest.writeString(totalPoints);
      dest.writeString(receiptURL);
   }

   public static final Creator<ThrstPaymentBundle> CREATOR = new Creator<ThrstPaymentBundle>() {
      @Override
      public ThrstPaymentBundle createFromParcel(Parcel in) {
         return new ThrstPaymentBundle(in);
      }

      @Override
      public ThrstPaymentBundle[] newArray(int size) {
         return new ThrstPaymentBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }
}
