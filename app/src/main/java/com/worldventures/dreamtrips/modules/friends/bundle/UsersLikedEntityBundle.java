package com.worldventures.dreamtrips.modules.friends.bundle;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class UsersLikedEntityBundle extends BaseUsersBundle {

   private FeedEntity feedEntity;
   private int likersCount;

   public UsersLikedEntityBundle(FeedEntity feedEntity, int likersCount) {
      this.feedEntity = feedEntity;
      this.likersCount = likersCount;
   }

   public FeedEntity getFeedEntity() {
      return feedEntity;
   }

   public int getLikersCount() {
      return likersCount;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(this.feedEntity);
      dest.writeInt(this.likersCount);
   }

   protected UsersLikedEntityBundle(Parcel in) {
      this.feedEntity = (FeedEntity) in.readSerializable();
      this.likersCount = in.readInt();
   }

   public static final Creator<UsersLikedEntityBundle> CREATOR = new Creator<UsersLikedEntityBundle>() {
      @Override
      public UsersLikedEntityBundle createFromParcel(Parcel source) {return new UsersLikedEntityBundle(source);}

      @Override
      public UsersLikedEntityBundle[] newArray(int size) {return new UsersLikedEntityBundle[size];}
   };
}
