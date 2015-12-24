package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class DtlMerchantInfoInflater extends DtlMerchantDataInflater {

    protected DtlMerchantHelper helper;
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

    public DtlMerchantInfoInflater(DtlMerchantHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void onMerchantApply(DtlMerchant merchant) {
        title.setText(merchant.getDisplayName());
        pricing.setRating(merchant.getBudget());

        if (merchant.getDistance() != 0.0d) {
            distance.setVisibility(View.VISIBLE);
            distance.setText(distance.getResources().getString(
                    merchant.getDistanceType() == DtlFilterData.DistanceType.MILES ?
                            R.string.distance_miles : R.string.distance_kms,
                    merchant.getDistance()));
        } else distance.setVisibility(View.GONE);

        String categoriesString = helper.getCategories(merchant);
        if (!TextUtils.isEmpty(categoriesString)) {
            categories.setVisibility(View.VISIBLE);
            categories.setText(categoriesString);
        } else categories.setVisibility(View.GONE);

        if (merchant.hasOffer(DtlOffer.TYPE_POINTS) &&
                merchant.getOperationDays() != null && !merchant.getOperationDays().isEmpty()) {
            operationalTime.setVisibility(View.VISIBLE);
            operationalTime.setText(helper.getOperationalTime(merchant));
        } else operationalTime.setVisibility(View.GONE);
    }
}
