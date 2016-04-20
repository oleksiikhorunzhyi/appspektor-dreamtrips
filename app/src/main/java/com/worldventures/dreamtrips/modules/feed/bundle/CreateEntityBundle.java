package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;

public class CreateEntityBundle implements Parcelable {

    private MediaAttachment mediaAttachment;
    private boolean showPickerImmediately;

    public CreateEntityBundle(boolean showPickerImmediately) {
        this.showPickerImmediately = showPickerImmediately;
    }

    public CreateEntityBundle(MediaAttachment mediaAttachment) {
        this.mediaAttachment = mediaAttachment;
    }

    public MediaAttachment getMediaAttachment() {
        return mediaAttachment;
    }

    public boolean isShowPickerImmediately() {
        return showPickerImmediately;
    }

    public void setShowPickerImmediately(boolean showPickerImmediately) {
        this.showPickerImmediately = showPickerImmediately;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mediaAttachment, flags);
        dest.writeByte(showPickerImmediately ? (byte) 1 : (byte) 0);
    }

    protected CreateEntityBundle(Parcel in) {
        this.mediaAttachment = in.readParcelable(MediaAttachment.class.getClassLoader());
        this.showPickerImmediately = in.readByte() != 0;
    }

    public static final Creator<CreateEntityBundle> CREATOR = new Creator<CreateEntityBundle>() {
        @Override
        public CreateEntityBundle createFromParcel(Parcel source) {
            return new CreateEntityBundle(source);
        }

        @Override
        public CreateEntityBundle[] newArray(int size) {
            return new CreateEntityBundle[size];
        }
    };
}
