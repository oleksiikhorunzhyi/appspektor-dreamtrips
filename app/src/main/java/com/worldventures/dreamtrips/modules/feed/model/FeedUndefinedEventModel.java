package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

public class FeedUndefinedEventModel extends BaseEventModel<TripModel> {

    public FeedUndefinedEventModel() {
    }

    public FeedUndefinedEventModel(Parcel in) {
        super(in);
    }

    public static final Creator<FeedUndefinedEventModel> CREATOR = new Creator<FeedUndefinedEventModel>() {
        @Override
        public FeedUndefinedEventModel createFromParcel(Parcel in) {
            return new FeedUndefinedEventModel(in);
        }

        @Override
        public FeedUndefinedEventModel[] newArray(int size) {
            return new FeedUndefinedEventModel[size];
        }
    };
}
