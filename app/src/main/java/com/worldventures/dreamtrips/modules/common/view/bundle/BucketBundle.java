package com.worldventures.dreamtrips.modules.common.view.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class BucketBundle implements Parcelable {

    protected BucketItem.BucketType type;
    protected BucketItem bucketItem;
    protected int ownerId;
    //
    protected boolean lock;
    protected boolean slave;


    public void setType(BucketItem.BucketType type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = BucketItem.BucketType.valueOf(type.toUpperCase());
    }

    public BucketItem.BucketType getType() {
        return type;
    }

    public void setBucketItem(BucketItem bucketItem) {
        this.bucketItem = bucketItem;
    }

    public BucketItem getBucketItem() {
        return bucketItem;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isSlave() {
        return slave;
    }

    public void setSlave(boolean slave) {
        this.slave = slave;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeSerializable(bucketItem);
        dest.writeInt(this.ownerId);
        dest.writeByte(lock ? (byte) 1 : (byte) 0);
        dest.writeByte(slave ? (byte) 1 : (byte) 0);
    }

    public BucketBundle() {
    }

    protected BucketBundle(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : BucketItem.BucketType.values()[tmpType];
        this.bucketItem = (BucketItem) in.readSerializable();
        this.ownerId = in.readInt();
        this.lock = in.readByte() != 0;
        this.slave = in.readByte() != 0;
    }

    public static final Parcelable.Creator<BucketBundle> CREATOR = new Parcelable.Creator<BucketBundle>() {
        public BucketBundle createFromParcel(Parcel source) {
            return new BucketBundle(source);
        }

        public BucketBundle[] newArray(int size) {
            return new BucketBundle[size];
        }
    };
}