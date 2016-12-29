package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class MerchantCommonDataInflater extends MerchantDataInflater {

   @InjectView(R.id.merchant_details_cover_stub) View coverStub;
   @InjectView(R.id.merchant_details_rating) ProperRatingBar rating;
   @InjectView(R.id.merchant_details_points_badge) ImageView earnPointsBadge;
   @InjectView(R.id.perks_only_badge) ImageView perksOnlyBadge;
   @InjectView(R.id.perks_caption) TextView perksCaption;

   @Override
   protected void onMerchantAttributesApply() {
      ViewUtils.setViewVisibility(coverStub, merchantAttributes.images() == null ? View.VISIBLE : View.GONE);

      if (merchantAttributes.rating() != 0.0d) {
         ViewUtils.setViewVisibility(rating, View.VISIBLE);
         rating.setRating(Double.valueOf(merchantAttributes.rating()).intValue());
      } else ViewUtils.setViewVisibility(rating, View.GONE);

      if (!merchantAttributes.hasOffers()) {
         earnPointsBadge.setVisibility(View.GONE);
         perksOnlyBadge.setVisibility(View.GONE);
         perksCaption.setVisibility(View.GONE);
         return;
      }

      setupPointsBadge();
      setupPerksBadge();
   }

   private void setupPointsBadge() {
      if (!merchantAttributes.hasPoints()) {
         earnPointsBadge.setVisibility(View.GONE);
         return;
      }

      earnPointsBadge.setImageResource(merchantAttributes.hasPerks() ?
            R.drawable.ic_dtl_points_and_perks_badge : R.drawable.ic_dtl_points_badge);
   }

   private void setupPerksBadge() {
      if (!merchantAttributes.hasPerks()) {
         perksOnlyBadge.setVisibility(View.GONE);
         perksCaption.setVisibility(View.GONE);
         return;
      }

      ViewUtils.setViewVisibility(perksOnlyBadge, merchantAttributes.hasPoints() ? View.GONE : View.VISIBLE);
   }
}
