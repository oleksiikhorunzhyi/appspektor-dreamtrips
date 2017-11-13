package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

public class ThrstPaymentCompletedBundle implements Parcelable {

   private final Merchant merchant;
   private final String earnedPoints;
   private final String totalPoints;

   public ThrstPaymentCompletedBundle(Merchant merchant, String earnedPoints, String totalPoints) {
      this.merchant = merchant;
      this.earnedPoints = earnedPoints;
      this.totalPoints = totalPoints;
   }

   public Merchant getMerchant() {
      return merchant;
   }

   public String getEarnedPoints() {
      return earnedPoints;
   }

   public String getTotalPoints() {
      return totalPoints;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable part
   ///////////////////////////////////////////////////////////////////////////

   protected ThrstPaymentCompletedBundle(Parcel in) {
      merchant = (Merchant) in.readSerializable();
      earnedPoints = in.readString();
      totalPoints = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(merchant);
      dest.writeString(earnedPoints);
      dest.writeString(totalPoints);
   }

   public static final Creator<ThrstPaymentCompletedBundle> CREATOR = new Creator<ThrstPaymentCompletedBundle>() {
      @Override
      public ThrstPaymentCompletedBundle createFromParcel(Parcel in) {
         return new ThrstPaymentCompletedBundle(in);
      }

      @Override
      public ThrstPaymentCompletedBundle[] newArray(int size) {
         return new ThrstPaymentCompletedBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }
}
