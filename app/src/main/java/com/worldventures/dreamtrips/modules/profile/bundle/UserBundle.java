package com.worldventures.dreamtrips.modules.profile.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.User;

public class UserBundle implements Parcelable {

   private User user;
   private int notificationId; // to mark notification as read when routing from push
   private boolean acceptFriend;

   public static final int NO_NOTIFICATION = -1;

   public UserBundle(User user) {
      this(user, NO_NOTIFICATION);
   }

   public UserBundle(User user, int notificationId) {
      this(user, notificationId, false);
   }

   public UserBundle(User user, int notificationId, boolean acceptFriend) {
      this.user = user;
      this.notificationId = notificationId;
      this.acceptFriend = acceptFriend;
      //
      if (notificationId == 0) this.notificationId = NO_NOTIFICATION;
   }

   public User getUser() {
      return user;
   }

   public int getNotificationId() {
      return notificationId;
   }

   public void resetNotificationId() {
      this.notificationId = NO_NOTIFICATION;
   }

   public boolean isAcceptFriend() {
      return acceptFriend;
   }

   public void resetAcceptFriend() {
      acceptFriend = false;
   }

   protected UserBundle(Parcel in) {
      user = in.readParcelable(User.class.getClassLoader());
      notificationId = in.readInt();
      acceptFriend = in.readInt() == 1;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeParcelable(user, i);
      parcel.writeInt(notificationId);
      parcel.writeInt(acceptFriend ? 1 : 0);
   }

   public static final Creator<UserBundle> CREATOR = new Creator<UserBundle>() {
      @Override
      public UserBundle createFromParcel(Parcel in) {
         return new UserBundle(in);
      }

      @Override
      public UserBundle[] newArray(int size) {
         return new UserBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }
}
