package com.worldventures.dreamtrips.modules.dtl.helper;

import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class DtlPlaceCategoryDataInflater extends DtlPlaceDataInflater {

    protected DtlPlaceHelper helper;
    //
    @InjectView(R.id.place_details_category)
    TextView category;
    @InjectView(R.id.place_details_pricing)
    ProperRatingBar pricing;

    public DtlPlaceCategoryDataInflater(DtlPlaceHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void onPlaceApply(DtlPlace place) {
        category.setText(helper.getFirstCategoryName(place));
        pricing.setRating(place.getBudget());
    }
}
