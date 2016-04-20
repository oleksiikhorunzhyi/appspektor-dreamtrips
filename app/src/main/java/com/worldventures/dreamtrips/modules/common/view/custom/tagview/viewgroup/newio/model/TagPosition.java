package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TagPosition implements Parcelable, Serializable {

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

    public Position getTopLeft() {
        return topLeft;
    }

    public Position getBottomRight() {
        return bottomRight;
    }

    protected TagPosition(Parcel in) {
        topLeft = in.readParcelable(Position.class.getClassLoader());
        bottomRight = in.readParcelable(Position.class.getClassLoader());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagPosition that = (TagPosition) o;

        if (topLeft != null ? !topLeft.equals(that.topLeft) : that.topLeft != null)
            return false;
        return bottomRight != null ? bottomRight.equals(that.bottomRight) : that.bottomRight == null;

    }

    @Override
    public int hashCode() {
        int result = topLeft != null ? topLeft.hashCode() : 0;
        result = 31 * result + (bottomRight != null ? bottomRight.hashCode() : 0);
        return result;
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