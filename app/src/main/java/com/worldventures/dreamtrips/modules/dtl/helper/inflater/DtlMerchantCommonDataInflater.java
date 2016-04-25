package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class DtlMerchantCommonDataInflater extends DtlMerchantDataInflater {

    protected DtlMerchantHelper helper;
    //
    @InjectView(R.id.merchant_details_cover_stub)
    View coverStub;
    @InjectView(R.id.merchant_details_rating)
    ProperRatingBar rating;
    @InjectView(R.id.merchant_details_points_badge)
    ImageView earnPointsBadge;
    @InjectView(R.id.perks)
    TextView perks;

    @Override
    protected void onMerchantApply(DtlMerchant merchant) {
        coverStub.setVisibility(merchant.getImages().isEmpty() ? View.VISIBLE : View.GONE);
        if (merchant.getRating() != 0.0f) {
            rating.setVisibility(View.VISIBLE);
            rating.setRating(Float.valueOf(merchant.getRating()).intValue());
        } else {
            rating.setVisibility(View.GONE);
        }

        int perkMargin = merchant.hasOffer(DtlOffer.TYPE_POINTS) ?
                rootView.getResources().getDimensionPixelSize(R.dimen.perks_margin) : 0;
        ((LinearLayout.LayoutParams) perks.getLayoutParams()).setMargins(perkMargin, 0, 0, 0);
        //
        perks.setVisibility(merchant.hasOffer(DtlOffer.TYPE_PERK) ? View.VISIBLE : View.GONE);
        earnPointsBadge.setVisibility(merchant.hasOffer(DtlOffer.TYPE_POINTS) ? View.VISIBLE : View.GONE);
    }


}
