package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;

public class FeedPostEventModel extends BaseEventModel<TextualPost> {

    public FeedPostEventModel() {
    }

    public FeedPostEventModel(Parcel in) {
        super(in);
    }

    public static final Creator<FeedPostEventModel> CREATOR = new Creator<FeedPostEventModel>() {
        @Override
        public FeedPostEventModel createFromParcel(Parcel in) {
            return new FeedPostEventModel(in);
        }

        @Override
        public FeedPostEventModel[] newArray(int size) {
            return new FeedPostEventModel[size];
        }
    };
}
