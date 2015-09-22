package com.worldventures.dreamtrips.modules.profile.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.User;

public class UserBundle implements Parcelable {

    private User user;

    //for mark notification as read when routing from push
    private int notificationId;

    public UserBundle(User user) {
        this.user = user;
    }

    public UserBundle(User user, int notificationId) {
        this.user = user;
        this.notificationId = notificationId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public User getUser() {
        return user;
    }

    protected UserBundle(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        notificationId = in.readInt();
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(user, i);
        parcel.writeInt(notificationId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
