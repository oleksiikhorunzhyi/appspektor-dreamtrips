package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class MerchantInfoInflater extends MerchantDataInflater {

    @InjectView(R.id.operational_time) TextView operationalTime;
    @InjectView(R.id.merchant_details_pricing) ProperRatingBar pricing;
    @InjectView(R.id.categories) TextView categories;
    @InjectView(R.id.distance) TextView distance;

    protected Resources resources;

    @Override
    public void setView(View rootView) {
        super.setView(rootView);
        this.resources = rootView.getResources();
    }

    @Override
    protected void onMerchantApply() {
        setInfo();
    }

    private void setInfo() {
        pricing.setRating(merchant.getBudget());
        //
        boolean hasDistance = merchant.getDistance() != 0.0d;
        boolean hasOperationDays = merchant.hasPoints() && merchant.getOperationDays() != null && !merchant.getOperationDays().isEmpty();
        boolean hasCategories = !TextUtils.isEmpty(DtlMerchantHelper.getCategories(merchant));

        CharSequence distanceText = hasDistance ? resources.getString(R.string.distance_caption_format, merchant.getDistance(),
                resources.getString(merchant.getDistanceType() == DistanceType.MILES ? R.string.mi : R.string.km)) : "";

        CharSequence operDays = hasOperationDays ? DtlMerchantHelper.getOperationalTime(rootView.getContext(), merchant) : "";
        CharSequence categoriesText = hasCategories ? DtlMerchantHelper.getCategories(merchant) : "";

        ViewUtils.setTextOrHideView(distance, distanceText);
        ViewUtils.setTextOrHideView(categories, categoriesText);
        ViewUtils.setTextOrHideView(operationalTime, operDays);
    }

}
