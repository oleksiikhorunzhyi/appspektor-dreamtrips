package com.worldventures.dreamtrips.modules.friends.bundle;

import android.os.Parcel;

public class MutualFriendsBundle extends BaseUsersBundle {

   private int id;

   public MutualFriendsBundle(int id) {
      this.id = id;
   }

   protected MutualFriendsBundle(Parcel in) {
      id = in.readInt();
   }

   public static final Creator<MutualFriendsBundle> CREATOR = new Creator<MutualFriendsBundle>() {
      @Override
      public MutualFriendsBundle createFromParcel(Parcel in) {
         return new MutualFriendsBundle(in);
      }

      @Override
      public MutualFriendsBundle[] newArray(int size) {
         return new MutualFriendsBundle[size];
      }
   };

   public int getId() {
      return id;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(id);
   }
}
