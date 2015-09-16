package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

public class PostBundle implements Parcelable {

    private TextualPost textualPost;

    public PostBundle(TextualPost textualPost) {
        this.textualPost = textualPost;
    }

    protected PostBundle(Parcel in) {
        textualPost = (TextualPost) in.readSerializable();
    }

    public static final Creator<PostBundle> CREATOR = new Creator<PostBundle>() {
        @Override
        public PostBundle createFromParcel(Parcel in) {
            return new PostBundle(in);
        }

        @Override
        public PostBundle[] newArray(int size) {
            return new PostBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(textualPost);
    }

    public TextualPost getTextualPost() {
        return textualPost;
    }
}
