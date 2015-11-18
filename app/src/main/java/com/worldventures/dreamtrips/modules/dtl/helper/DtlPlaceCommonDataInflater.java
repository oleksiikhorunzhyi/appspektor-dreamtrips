package com.worldventures.dreamtrips.modules.dtl.helper;

import android.view.View;
import android.widget.ImageView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class DtlPlaceCommonDataInflater extends DtlPlaceDataInflater {

    protected DtlPlaceHelper helper;
    //
    @InjectView(R.id.place_details_cover_stub)
    View coverStub;
    @InjectView(R.id.place_details_rating)
    ProperRatingBar rating;
    @InjectView(R.id.place_details_points_badge)
    ImageView earnPointsBadge;

    public DtlPlaceCommonDataInflater(DtlPlaceHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void onPlaceApply(DtlPlace place) {
        coverStub.setVisibility(place.getImages().isEmpty() ? View.VISIBLE : View.GONE);
        if (place.getRating() != 0.0f) {
            rating.setVisibility(View.VISIBLE);
            rating.setRating(Float.valueOf(place.getRating()).intValue());
        } else {
            rating.setVisibility(View.GONE);
        }
        earnPointsBadge.setVisibility(place.getPartnerStatus() == DtlPlaceType.OFFER ? View.VISIBLE : View.GONE);
    }
}
