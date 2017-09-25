package com.worldventures.dreamtrips.modules.bucketlist.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

public class BucketFullscreenBundle implements Parcelable {
   private BucketItem bucketItem;
   private BucketPhoto bucketPhoto;

   public BucketFullscreenBundle(BucketItem bucketItem, BucketPhoto bucketPhoto) {
      this.bucketItem = bucketItem;
      this.bucketPhoto = bucketPhoto;
   }

   public BucketItem getBucketItem() {
      return bucketItem;
   }

   public BucketPhoto getBucketPhoto() {
      return bucketPhoto;
   }

   protected BucketFullscreenBundle(Parcel in) {
      bucketItem = (BucketItem) in.readSerializable();
      bucketPhoto = (BucketPhoto) in.readSerializable();
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(bucketItem);
      dest.writeSerializable(bucketPhoto);
   }

   public static final Creator<BucketFullscreenBundle> CREATOR = new Creator<BucketFullscreenBundle>() {
      @Override
      public BucketFullscreenBundle createFromParcel(Parcel in) {
         return new BucketFullscreenBundle(in);
      }

      @Override
      public BucketFullscreenBundle[] newArray(int size) {
         return new BucketFullscreenBundle[size];
      }
   };
}
