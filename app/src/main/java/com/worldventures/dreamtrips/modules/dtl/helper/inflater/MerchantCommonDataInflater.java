package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class MerchantCommonDataInflater extends MerchantDataInflater {

   @InjectView(R.id.merchant_details_cover_stub) View coverStub;
   @InjectView(R.id.merchant_details_rating) ProperRatingBar rating;
   @InjectView(R.id.merchant_details_points_badge) ImageView earnPointsBadge;
   @InjectView(R.id.perks) TextView perks;

   @Override
   protected void onMerchantApply() {
      ViewUtils.setViewVisibility(coverStub, merchant.images() == null ? View.VISIBLE : View.GONE);
      //
      if (merchant.rating() != 0.0d) {
         ViewUtils.setViewVisibility(rating, View.VISIBLE);
         rating.setRating(Double.valueOf(merchant.rating()).intValue());
      } else ViewUtils.setViewVisibility(rating, View.GONE);
      //
      int offerMargin = MerchantHelper.merchantHasPoints(merchant) ? rootView.getResources().getDimensionPixelSize(R.dimen.perks_margin) : 0;
      ((LinearLayout.LayoutParams) perks.getLayoutParams()).setMargins(offerMargin, 0, 0, 0);
      //
      ViewUtils.setViewVisibility(perks, MerchantHelper.merchantHasPerks(merchant) ? View.VISIBLE : View.GONE);
      ViewUtils.setViewVisibility(earnPointsBadge, MerchantHelper.merchantHasPoints(merchant) ? View.VISIBLE : View.GONE);
   }
}
