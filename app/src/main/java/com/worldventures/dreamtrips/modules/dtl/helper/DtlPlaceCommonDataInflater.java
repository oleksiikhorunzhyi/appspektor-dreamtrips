package com.worldventures.dreamtrips.modules.dtl.helper;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
        rating.setRating(Float.valueOf(place.getRating()).intValue());
        earnPointsBadge.setVisibility(place.getPartnerStatus() == DtlPlaceType.OFFER ? View.VISIBLE : View.GONE);
    }
}
