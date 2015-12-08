package com.worldventures.dreamtrips.modules.dtl.helper;

import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class DtlMerchantInfoInflater extends DtlMerchantDataInflater {

    protected DtlMerchantHelper helper;
    //
    @InjectView(R.id.merchant_title)
    TextView title;
    @InjectView(R.id.operational_time)
    TextView operationalTime;
    @InjectView(R.id.merchant_details_pricing)
    ProperRatingBar pricing;

    public DtlMerchantInfoInflater(DtlMerchantHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void onMerchantApply(DtlMerchant merchant) {
        title.setText(merchant.getDisplayName());
        pricing.setRating(merchant.getBudget());

        if (merchant.hasOffer(DtlOffer.TYPE_POINTS)) {
            operationalTime.setVisibility(View.VISIBLE);
            operationalTime.setText(helper.getOperationalTime(merchant));
        } else operationalTime.setVisibility(View.GONE);
    }
}
