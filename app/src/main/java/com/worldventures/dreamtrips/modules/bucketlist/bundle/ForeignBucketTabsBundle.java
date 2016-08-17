package com.worldventures.dreamtrips.modules.bucketlist.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.User;

public class ForeignBucketTabsBundle implements Parcelable {

   private User user;

   public ForeignBucketTabsBundle(User user) {
      this.user = user;
   }


   public User getUser() {
      return user;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.user, 0);
   }

   protected ForeignBucketTabsBundle(Parcel in) {
      this.user = in.readParcelable(User.class.getClassLoader());
   }

   public static final Creator<ForeignBucketTabsBundle> CREATOR = new Creator<ForeignBucketTabsBundle>() {
      public ForeignBucketTabsBundle createFromParcel(Parcel source) {
         return new ForeignBucketTabsBundle(source);
      }

      public ForeignBucketTabsBundle[] newArray(int size) {
         return new ForeignBucketTabsBundle[size];
      }
   };
}
