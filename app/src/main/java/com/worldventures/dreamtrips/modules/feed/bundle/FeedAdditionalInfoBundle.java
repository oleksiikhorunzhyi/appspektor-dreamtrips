package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.User;

public class FeedAdditionalInfoBundle implements Parcelable {

   User user;

   public FeedAdditionalInfoBundle(User user) {
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

   protected FeedAdditionalInfoBundle(Parcel in) {
      this.user = in.readParcelable(User.class.getClassLoader());
   }

   public static final Creator<FeedAdditionalInfoBundle> CREATOR = new Creator<FeedAdditionalInfoBundle>() {
      public FeedAdditionalInfoBundle createFromParcel(Parcel source) {
         return new FeedAdditionalInfoBundle(source);
      }

      public FeedAdditionalInfoBundle[] newArray(int size) {
         return new FeedAdditionalInfoBundle[size];
      }
   };
}
