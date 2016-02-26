package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder.Type;

public class EditEntityBundle implements Parcelable {

    private FeedEntity entity;
    private Type type;

    public EditEntityBundle(FeedEntity entity, Type type) {
        this.entity = entity;
        this.type = type;
    }

    public FeedEntity getEntity() {
        return entity;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.entity);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    protected EditEntityBundle(Parcel in) {
        this.entity = (FeedEntity) in.readSerializable();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
    }

    public static final Creator<EditEntityBundle> CREATOR = new Creator<EditEntityBundle>() {
        public EditEntityBundle createFromParcel(Parcel source) {
            return new EditEntityBundle(source);
        }

        public EditEntityBundle[] newArray(int size) {
            return new EditEntityBundle[size];
        }
    };
}
