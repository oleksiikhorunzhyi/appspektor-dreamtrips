package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

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
    @InjectView(R.id.categories)
    TextView categories;
    @InjectView(R.id.distance)
    TextView distance;

    public DtlPlaceInfoInflater(DtlPlaceHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void onPlaceApply(DtlMerchant place) {
        title.setText(place.getDisplayName());
        pricing.setRating(place.getBudget());

        if (place.getDistance() != 0.0d) {
            distance.setVisibility(View.VISIBLE);
            distance.setText(distance.getResources().getString(
                    place.getDistanceType() == DtlFilterData.DistanceType.MILES ?
                            R.string.distance_miles : R.string.distance_kms,
                    place.getDistance()));
        } else distance.setVisibility(View.GONE);

        String categoriesString = helper.getCategories(place);
        if (!TextUtils.isEmpty(categoriesString)) {
            categories.setVisibility(View.VISIBLE);
            categories.setText(categoriesString);
        } else categories.setVisibility(View.GONE);

        if (place.hasOffer(DtlOffer.TYPE_POINTS) &&
                place.getOperationDays() != null && !place.getOperationDays().isEmpty()) {
            operationalTime.setVisibility(View.VISIBLE);
            operationalTime.setText(helper.getOperationalTime(place));
        } else operationalTime.setVisibility(View.GONE);
    }
}
