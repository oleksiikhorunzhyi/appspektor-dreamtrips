package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PostBundle implements Parcelable {

    public static final int ANY = 1;
    public static final int PHOTO = 2;

    private TextualPost textualPost;
    private int postType;
    private boolean showPickerImmediately;

    public PostBundle(TextualPost textualPost) {
        this(textualPost, ANY);
    }

    public PostBundle(TextualPost textualPost, @PostType int postType) {
        this(textualPost, postType, false);
    }

    public PostBundle(TextualPost textualPost, @PostType int postType, boolean showPickerImmediately) {
        this.textualPost = textualPost;
        this.postType = postType;
        this.showPickerImmediately = showPickerImmediately;
    }

    protected PostBundle(Parcel in) {
        textualPost = (TextualPost) in.readSerializable();
        postType = in.readInt();
        showPickerImmediately = in.readByte() == 1;
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
        dest.writeInt(postType);
        dest.writeByte((byte) (showPickerImmediately ? 1 : 0));
    }

    public TextualPost getTextualPost() {
        return textualPost;
    }

    @PostType
    public int getType() {
        return postType;
    }

    public boolean isShowPickerImmediately() {
        return showPickerImmediately;
    }

    @IntDef({ANY, PHOTO})
    @Retention(RetentionPolicy.SOURCE)
    @interface PostType {
    }
}
