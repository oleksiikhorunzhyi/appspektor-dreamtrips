package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class ThrstPaymentBundle implements Parcelable {

   private final boolean isPaid;
   private final String totalAmount;

   public ThrstPaymentBundle(boolean isPaid, String totalAmount) {
      this.isPaid = isPaid;
      this.totalAmount = totalAmount;
   }

   public boolean isPaid() {
      return isPaid;
   }

   public String getTotalAmount() {
      return totalAmount;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable part
   ///////////////////////////////////////////////////////////////////////////

   protected ThrstPaymentBundle(Parcel in) {
      isPaid = in.readByte() != 0;
      totalAmount = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte((byte) (isPaid ? 1 : 0));
      dest.writeString(totalAmount);
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
