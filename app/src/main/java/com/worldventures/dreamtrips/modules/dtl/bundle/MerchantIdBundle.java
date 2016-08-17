package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class MerchantIdBundle implements Parcelable {

   private String merchantId;

   public MerchantIdBundle(String merchantId) {
      this.merchantId = merchantId;
   }

   public String getMerchantId() {
      return merchantId;
   }

   public void setMerchantId(String merchantId) {
      this.merchantId = merchantId;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable part
   ///////////////////////////////////////////////////////////////////////////

   protected MerchantIdBundle(Parcel in) {
      merchantId = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(merchantId);
   }

   public static final Creator<MerchantIdBundle> CREATOR = new Creator<MerchantIdBundle>() {
      @Override
      public MerchantIdBundle createFromParcel(Parcel in) {
         return new MerchantIdBundle(in);
      }

      @Override
      public MerchantIdBundle[] newArray(int size) {
         return new MerchantIdBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }
}
