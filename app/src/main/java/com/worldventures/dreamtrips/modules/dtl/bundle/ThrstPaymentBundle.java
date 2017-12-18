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
   private final double subTotalAmount;
   private final double taxAmount;
   private final double tipAmount;

   public ThrstPaymentBundle(Merchant merchant, boolean isPaid, String totalAmount,
         String earnedPoints, String totalPoints, String receiptURL, double subTotalAmount,
         double taxAmount, double tipAmount) {
      this.merchant = merchant;
      this.isPaid = isPaid;
      this.totalAmount = totalAmount;
      this.earnedPoints = earnedPoints;
      this.totalPoints = totalPoints;
      this.receiptURL = receiptURL;
      this.subTotalAmount = subTotalAmount;
      this.taxAmount = taxAmount;
      this.tipAmount = tipAmount;
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

   public double getSubTotalAmount() {
      return subTotalAmount;
   }

   public double getTaxAmount() {
      return taxAmount;
   }

   public double getTipAmount() {
      return tipAmount;
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
      subTotalAmount = in.readDouble();
      taxAmount = in.readDouble();
      tipAmount = in.readDouble();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(merchant);
      dest.writeByte((byte) (isPaid ? 1 : 0));
      dest.writeString(totalAmount);
      dest.writeString(earnedPoints);
      dest.writeString(totalPoints);
      dest.writeString(receiptURL);
      dest.writeDouble(subTotalAmount);
      dest.writeDouble(taxAmount);
      dest.writeDouble(tipAmount);
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
