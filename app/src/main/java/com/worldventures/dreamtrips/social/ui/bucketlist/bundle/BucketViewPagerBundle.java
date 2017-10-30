package com.worldventures.dreamtrips.social.ui.bucketlist.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

public class BucketViewPagerBundle implements Parcelable {

   private final BucketItem bucketItem;
   private final int selection;

   public BucketViewPagerBundle(BucketItem bucketItem, int selection) {
      this.bucketItem = bucketItem;
      this.selection = selection;
   }

   public BucketItem getBucketItem() {
      return bucketItem;
   }

   public int getSelection() {
      return selection;
   }

   protected BucketViewPagerBundle(Parcel in) {
      bucketItem = (BucketItem) in.readSerializable();
      selection = in.readInt();
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(bucketItem);
      dest.writeInt(selection);
   }

   public static final Creator<BucketViewPagerBundle> CREATOR = new Creator<BucketViewPagerBundle>() {
      @Override
      public BucketViewPagerBundle createFromParcel(Parcel in) {
         return new BucketViewPagerBundle(in);
      }

      @Override
      public BucketViewPagerBundle[] newArray(int size) {
         return new BucketViewPagerBundle[size];
      }
   };
}
