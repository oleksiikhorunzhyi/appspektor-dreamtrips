package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

public class ThrstFlowBundle implements Parcelable {

   private final Merchant merchant;

   public ThrstFlowBundle(Merchant merchant) {
      this.merchant = merchant;
   }

   public Merchant getMerchant() {
      return merchant;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable part
   ///////////////////////////////////////////////////////////////////////////

   protected ThrstFlowBundle(Parcel in) {
      merchant = (Merchant) in.readSerializable();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(merchant);
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
