package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

public class EditPostBundle implements Parcelable {

    private TextualPost post;

    public EditPostBundle(TextualPost post) {
        this.post = post;
    }

    public TextualPost getPost() {
        return post;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.post);
    }

    protected EditPostBundle(Parcel in) {
        this.post = (TextualPost) in.readSerializable();
    }

    public static final Creator<EditPostBundle> CREATOR = new Creator<EditPostBundle>() {
        @Override
        public EditPostBundle createFromParcel(Parcel source) {
            return new EditPostBundle(source);
        }

        @Override
        public EditPostBundle[] newArray(int size) {
            return new EditPostBundle[size];
        }
    };
}
