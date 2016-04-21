package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class DtlMerchantInfoInflater extends DtlMerchantDataInflater {

    @InjectView(R.id.merchant_title)
    TextView title;
    @InjectView(R.id.operational_time)
    TextView operationalTime;
    @InjectView(R.id.merchant_details_pricing)
    ProperRatingBar pricing;
    @InjectView(R.id.categories)
    TextView categories;
    @InjectView(R.id.distance)
    TextView distance;

    @Override
    protected void onMerchantApply(DtlMerchant merchant) {
        title.setText(merchant.getDisplayName());
        pricing.setRating(merchant.getBudget());
        //
        Resources res = distance.getResources();
        if (merchant.getDistance() != 0.0d) {
            distance.setVisibility(View.VISIBLE);
            distance.setText(res.getString(
                    R.string.distance_caption_format,
                    merchant.getDistance(),
                    res.getString(merchant.getDistanceType() == DistanceType.MILES ? R.string.mi : R.string.km)));
        } else distance.setVisibility(View.GONE);
        //
        String categoriesString = DtlMerchantHelper.getCategories(merchant);
        if (!TextUtils.isEmpty(categoriesString)) {
            categories.setVisibility(View.VISIBLE);
            categories.setText(categoriesString);
        } else categories.setVisibility(View.GONE);
        //
        if (merchant.hasOffer(DtlOffer.TYPE_POINTS) &&
                merchant.getOperationDays() != null && !merchant.getOperationDays().isEmpty()) {
            operationalTime.setVisibility(View.VISIBLE);
            operationalTime.setText(DtlMerchantHelper.getOperationalTime(rootView.getContext(), merchant));
        } else operationalTime.setVisibility(View.GONE);
    }
}
