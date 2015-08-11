package com.worldventures.dreamtrips.modules.friends.model;

import android.os.Parcel;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Friend extends User {

    @SerializedName("circle_ids")
    HashSet<String> circleIds;

    @SerializedName("mutual_friends")
    int mutualFriends;

    private transient String circles;

    public int getMutualFriends() {
        return mutualFriends;
    }

    public void setCircles(List<Circle> circles) {
        List<String> userCircles = new ArrayList<>();

        for (String s : circleIds) {
            for (Circle circle : circles) {
                if (circle.getId() != null && circle.getId().equals(s)) {
                    userCircles.add(circle.getName());
                    break;
                }
            }
        }

        this.circles = TextUtils.join(", ", userCircles);
    }

    public String getCircles() {
        return circles;
    }

    public HashSet<String> getCircleIds() {
        return circleIds;
    }

    public Friend() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeSerializable(this.circleIds);
        dest.writeInt(this.mutualFriends);
        dest.writeString(this.circles);
    }

    protected Friend(Parcel in) {
        super(in);
        this.circleIds = (HashSet<String>) in.readSerializable();
        this.mutualFriends = in.readInt();
        this.circles = in.readString();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        public Friend createFromParcel(Parcel source) {
            return new Friend(source);
        }

        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };
}
