package com.worldventures.dreamtrips.modules.friends.bundle;

import android.os.Parcel;

public class UsersLikedEntityBundle extends BaseUsersBundle {

    private String uid;

    public UsersLikedEntityBundle(String uid) {
        this.uid = uid;
    }

    protected UsersLikedEntityBundle(Parcel in) {
        uid = in.readString();
    }

    public static final Creator<UsersLikedEntityBundle> CREATOR = new Creator<UsersLikedEntityBundle>() {
        @Override
        public UsersLikedEntityBundle createFromParcel(Parcel in) {
            return new UsersLikedEntityBundle(in);
        }

        @Override
        public UsersLikedEntityBundle[] newArray(int size) {
            return new UsersLikedEntityBundle[size];
        }
    };

    public String getUid() {
        return uid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
    }
}
