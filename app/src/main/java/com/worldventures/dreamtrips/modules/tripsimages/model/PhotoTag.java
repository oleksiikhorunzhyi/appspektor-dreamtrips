package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.io.Serializable;

public class PhotoTag implements Parcelable, Serializable, Cloneable {

    private int targetUserId;
    private TagPosition position;
    private User user;

    public PhotoTag() {

    }

    public PhotoTag(int targetUserId, TagPosition position) {
        this.targetUserId = targetUserId;
        this.position = position;
    }

    public PhotoTag(int targetUserId, TagPosition position, User user) {
        this(targetUserId, position);
        this.user = user;
    }

    protected PhotoTag(Parcel in) {
        targetUserId = in.readInt();
        position = in.readParcelable(TagPosition.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<PhotoTag> CREATOR = new Creator<PhotoTag>() {
        @Override
        public PhotoTag createFromParcel(Parcel in) {
            return new PhotoTag(in);
        }

        @Override
        public PhotoTag[] newArray(int size) {
            return new PhotoTag[size];
        }
    };

    public int getTargetUserId() {
        return targetUserId;
    }

    public TagPosition getPosition() {
        return position;
    }

    public User getUser() {
        return user;
    }

    public void setTagPosition(TagPosition position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(targetUserId);
        dest.writeParcelable(position, flags);
        dest.writeParcelable(user, flags);
    }

    public static class TagPosition implements Parcelable, Serializable {

        private Position topLeft;
        private Position bottomRight;

        public TagPosition() {

        }

        public TagPosition(Position topLeft, Position bottomRight) {
            this.topLeft = topLeft;
            this.bottomRight = bottomRight;
        }

        public TagPosition(float topLeftX, float topLeftY, float bottomRightX, float bottomRightY) {
            topLeft = new Position(topLeftX, topLeftY);
            bottomRight = new Position(bottomRightX, bottomRightY);
        }

        protected TagPosition(Parcel in) {
            topLeft = in.readParcelable(Position.class.getClassLoader());
            bottomRight = in.readParcelable(Position.class.getClassLoader());
        }

        public static final Creator<TagPosition> CREATOR = new Creator<TagPosition>() {
            @Override
            public TagPosition createFromParcel(Parcel in) {
                return new TagPosition(in);
            }

            @Override
            public TagPosition[] newArray(int size) {
                return new TagPosition[size];
            }
        };

        public Position getTopLeft() {
            return topLeft;
        }

        public Position getBottomRight() {
            return bottomRight;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(topLeft, flags);
            dest.writeParcelable(bottomRight, flags);
        }
    }

    public static class Position implements Parcelable, Serializable {

        private float x;
        private float y;

        public Position() {

        }

        public Position(float x, float y) {
            this.x = x;
            this.y = y;
        }

        protected Position(Parcel in) {
            x = in.readFloat();
            y = in.readFloat();
        }

        public static final Creator<Position> CREATOR = new Creator<Position>() {
            @Override
            public Position createFromParcel(Parcel in) {
                return new Position(in);
            }

            @Override
            public Position[] newArray(int size) {
                return new Position[size];
            }
        };

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(x);
            dest.writeFloat(y);
        }
    }
}
