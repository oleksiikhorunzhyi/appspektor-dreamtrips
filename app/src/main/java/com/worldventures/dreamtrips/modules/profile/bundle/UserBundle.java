package com.worldventures.dreamtrips.modules.profile.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.User;

public class UserBundle implements Parcelable {

    private User user;

    public UserBundle(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    protected UserBundle(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
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
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
