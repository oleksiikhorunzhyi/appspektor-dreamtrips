package com.worldventures.dreamtrips.modules.friends.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class FriendGlobalSearchBundle extends BaseUsersBundle implements Parcelable {

   String query;

   public FriendGlobalSearchBundle(String query) {
      this.query = query;
   }

   public String getQuery() {
      return query;
   }


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.query);
   }

   protected FriendGlobalSearchBundle(Parcel in) {
      this.query = in.readString();
   }

   public static final Creator<FriendGlobalSearchBundle> CREATOR = new Creator<FriendGlobalSearchBundle>() {
      public FriendGlobalSearchBundle createFromParcel(Parcel source) {
         return new FriendGlobalSearchBundle(source);
      }

      public FriendGlobalSearchBundle[] newArray(int size) {
         return new FriendGlobalSearchBundle[size];
      }
   };
}
