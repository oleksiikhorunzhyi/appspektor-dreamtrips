package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.io.Serializable;

public class PhotoTag implements Parcelable, Serializable, Cloneable {

    private int targetUserId;
    private TagPosition position;
    private User user;

    /**
     * For serialization
     */
    public PhotoTag() {
    }

    public PhotoTag(TagPosition position, User user) {
        this.position = position;
        this.user = user;
        if (user != null) {
            this.targetUserId = user.getId();
        }
    }

    public TagPosition getProportionalPosition() {
        return position;
    }

    public User getUser() {
        return user;
    }

    public void setTagPosition(TagPosition position) {
        this.position = position;
    }

    public static PhotoTag cloneTag(PhotoTag photoTag) {
        PhotoTag result = new PhotoTag();
        result.targetUserId = photoTag.targetUserId;
        result.position = photoTag.position;
        result.user = photoTag.user;
        return result;
    }

    public static class TagPosition implements Parcelable, Serializable {

        private Position topLeft;
        private Position bottomRight;

        /**
         * For serialization
         */
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

        public boolean intersected(TagPosition tagPosition) {
            if (this.getTopLeft().getX() > tagPosition.getBottomRight().getX() || this.getBottomRight().getX() < tagPosition.getTopLeft().getX() ||
                    this.getTopLeft().getY() > tagPosition.getBottomRight().getY() || this.getBottomRight().getY() < tagPosition.getTopLeft().getY()) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "TagPosition{" +
                    "topLeft=" + topLeft +
                    ", bottomRight=" + bottomRight +
                    '}';
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

        @Override
        public String toString() {
            return "Position{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.targetUserId);
        dest.writeParcelable(this.position, 0);
        dest.writeParcelable(this.user, 0);
    }

    protected PhotoTag(Parcel in) {
        this.targetUserId = in.readInt();
        this.position = in.readParcelable(TagPosition.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<PhotoTag> CREATOR = new Creator<PhotoTag>() {
        public PhotoTag createFromParcel(Parcel source) {
            return new PhotoTag(source);
        }

        public PhotoTag[] newArray(int size) {
            return new PhotoTag[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoTag photoTag = (PhotoTag) o;

        return user.equals(photoTag.user);

    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }
}
