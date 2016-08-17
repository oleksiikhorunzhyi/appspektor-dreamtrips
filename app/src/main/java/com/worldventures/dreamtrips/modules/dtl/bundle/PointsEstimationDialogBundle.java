package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class PointsEstimationDialogBundle implements Parcelable {

   private String merchantId;

   public PointsEstimationDialogBundle(String merchantId) {
      this.merchantId = merchantId;
   }

   public String getMerchantId() {
      return merchantId;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable part
   ///////////////////////////////////////////////////////////////////////////

   protected PointsEstimationDialogBundle(Parcel in) {
      merchantId = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(merchantId);
   }

   public static final Creator<PointsEstimationDialogBundle> CREATOR = new Creator<PointsEstimationDialogBundle>() {
      @Override
      public PointsEstimationDialogBundle createFromParcel(Parcel in) {
         return new PointsEstimationDialogBundle(in);
      }

      @Override
      public PointsEstimationDialogBundle[] newArray(int size) {
         return new PointsEstimationDialogBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }
}
