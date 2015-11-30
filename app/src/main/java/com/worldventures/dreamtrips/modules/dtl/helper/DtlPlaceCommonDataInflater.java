package com.worldventures.dreamtrips.modules.dtl.helper;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.Offer;

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
    @InjectView(R.id.perks)
    TextView perks;

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

        int perkMargin = place.hasOffer(Offer.POINT_REWARD) ?
                rootView.getResources().getDimensionPixelSize(R.dimen.perks_margin) : 0;
        ((LinearLayout.LayoutParams) perks.getLayoutParams()).setMargins(perkMargin, 0, 0, 0);
        //
        perks.setVisibility(place.hasOffer(Offer.PERKS) ? View.VISIBLE : View.GONE);
        earnPointsBadge.setVisibility(place.hasOffer(Offer.POINT_REWARD) ? View.VISIBLE : View.GONE);
    }
}