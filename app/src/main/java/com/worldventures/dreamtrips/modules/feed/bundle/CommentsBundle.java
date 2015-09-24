package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;

public class CommentsBundle implements Parcelable {
    BaseEventModel baseEventModel;
    boolean openKeyboard;

    public CommentsBundle(BaseEventModel baseEventModel) {
        this(baseEventModel, false);
    }

    public CommentsBundle(BaseEventModel baseEventModel, boolean openKeyboard) {
        this.baseEventModel = baseEventModel;
        this.openKeyboard = openKeyboard;
    }

    public BaseEventModel getBaseEventModel() {
        return baseEventModel;
    }

    public boolean isOpenKeyboard() {
        return openKeyboard;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.baseEventModel, 0);
        dest.writeByte(openKeyboard ? (byte) 1 : (byte) 0);
    }

    protected CommentsBundle(Parcel in) {
        this.baseEventModel = in.readParcelable(BaseEventModel.class.getClassLoader());
        this.openKeyboard = in.readByte() != 0;
    }

    public static final Creator<CommentsBundle> CREATOR = new Creator<CommentsBundle>() {
        public CommentsBundle createFromParcel(Parcel source) {
            return new CommentsBundle(source);
        }

        public CommentsBundle[] newArray(int size) {
            return new CommentsBundle[size];
        }
    };
}
