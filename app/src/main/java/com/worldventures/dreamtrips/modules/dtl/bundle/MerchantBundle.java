package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

public class MerchantBundle implements Parcelable {

   private final Merchant merchant;

   public MerchantBundle(Merchant merchant) {
      this.merchant = merchant;
   }

   public Merchant getMerchant() {
      return merchant;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable part
   ///////////////////////////////////////////////////////////////////////////

   protected MerchantBundle(Parcel in) {
      merchant = (Merchant) in.readSerializable();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(merchant);
   }

   public static final Creator<MerchantBundle> CREATOR = new Creator<MerchantBundle>() {
      @Override
      public MerchantBundle createFromParcel(Parcel in) {
         return new MerchantBundle(in);
      }

      @Override
      public MerchantBundle[] newArray(int size) {
         return new MerchantBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }
}
