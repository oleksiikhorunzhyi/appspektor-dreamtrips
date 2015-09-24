package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;
import android.os.Parcel;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

public class FeedTripEventModel extends BaseEventModel<TripModel> {

    public FeedTripEventModel() {
    }

    public FeedTripEventModel(Parcel in) {
        super(in);
    }

    @Override
    public String previewImage(Resources res) {
        return getItem().getImageUrl("THUMB");
    }

    public static final Creator<FeedTripEventModel> CREATOR = new Creator<FeedTripEventModel>() {
        @Override
        public FeedTripEventModel createFromParcel(Parcel in) {
            return new FeedTripEventModel(in);
        }

        @Override
        public FeedTripEventModel[] newArray(int size) {
            return new FeedTripEventModel[size];
        }
    };
}
