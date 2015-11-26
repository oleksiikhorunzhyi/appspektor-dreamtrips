package com.worldventures.dreamtrips.modules.dtl.helper;

import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.model.Offer;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class DtlPlaceInfoInflater extends DtlPlaceDataInflater {

    protected DtlPlaceHelper helper;
    //
    @InjectView(R.id.place_title)
    TextView title;
    @InjectView(R.id.operational_time)
    TextView operationalTime;
    @InjectView(R.id.place_details_pricing)
    ProperRatingBar pricing;

    public DtlPlaceInfoInflater(DtlPlaceHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void onPlaceApply(DtlPlace place) {
        title.setText(place.getDisplayName());
        pricing.setRating(place.getBudget());

        if (place.hasOffer(Offer.POINT_REWARD)) {
            operationalTime.setVisibility(View.VISIBLE);
            operationalTime.setText(helper.getOperationalTime(place));
        } else operationalTime.setVisibility(View.GONE);
    }
}
