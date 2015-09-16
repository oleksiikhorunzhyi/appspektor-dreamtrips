package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

public class FeedTripEventModel extends BaseEventModel<TripModel> {

    @Override
    public String previewImage(Resources res) {
        return getItem().getImageUrl("THUMB");
    }
}
