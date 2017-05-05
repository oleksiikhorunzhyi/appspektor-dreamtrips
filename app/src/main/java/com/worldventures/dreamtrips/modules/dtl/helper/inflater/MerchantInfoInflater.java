package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.content.res.Resources;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewSummary;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.ValidateReviewUtil;

import javax.inject.Inject;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;
import rx.Observable;

public class MerchantInfoInflater extends MerchantDataInflater {

   @InjectView(R.id.operational_time) TextView operationalTime;
   @InjectView(R.id.merchant_details_pricing) ProperRatingBar pricing;
   @InjectView(R.id.categories) TextView categories;
   @InjectView(R.id.distance) TextView distance;
   @InjectView(R.id.view_points) TextView points;
   @InjectView(R.id.view_perks) TextView perks;
   @InjectView(R.id.ratingBarReviews) RatingBar mRatingBar;
   @InjectView(R.id.text_view_rating) TextView textViewRating;

   @Inject SnappyRepository db;

   protected Resources resources;

   private static final float DEFAULT_RATING_VALUE = 0;

   public MerchantInfoInflater(Injector injector) {
      injector.inject(this);
   }

   @Override
   public void setView(View rootView) {
      super.setView(rootView);
      this.resources = rootView.getResources();
   }

   @Override
   protected void onMerchantAttributesApply() {
      setInfo();
      setOffersSection();
      ValidateReviewUtil.setUpRating(rootView.getContext(), merchantAttributes.reviewSummary(), mRatingBar, textViewRating);
   }

   private void setInfo() {
      pricing.setRating(merchantAttributes.budget());

      CharSequence distanceText = merchantAttributes.provideFormattedDistance(resources,
            FilterHelper.provideDistanceFromSettings(db));
      CharSequence categoriesText = merchantAttributes.provideFormattedCategories();

      ViewUtils.setTextOrHideView(distance, distanceText);
      ViewUtils.setTextOrHideView(categories, categoriesText);

      if (merchantAttributes.hasOperationDays()) {
         Observable.fromCallable(() -> merchantAttributes.provideFormattedOperationalTime(rootView.getContext(), true))
               .compose(RxLifecycle.bindView(rootView))
               .subscribe(operationalTime::setText, ex -> operationalTime.setVisibility(View.GONE));
      } else ViewUtils.setViewVisibility(operationalTime, View.GONE);

   }

   private void setOffersSection() {
      if (!merchantAttributes.hasOffers()) {
         ViewUtils.setViewVisibility(this.perks, View.GONE);
         ViewUtils.setViewVisibility(this.points, View.GONE);
      } else {
         ViewUtils.setViewVisibility(this.perks, View.VISIBLE);
         ViewUtils.setViewVisibility(this.points, View.VISIBLE);
         int perksNumber = merchantAttributes.offersCount(OfferType.PERK);
         setOfferBadges(perksNumber, merchantAttributes.offers().size() - perksNumber);
      }
   }

   private void setOfferBadges(int perks, int points) {
      int perkVisibility = perks > 0 ? View.VISIBLE : View.GONE;
      int pointVisibility = points > 0 ? View.VISIBLE : View.GONE;

      ViewUtils.setViewVisibility(this.perks, perkVisibility);
      ViewUtils.setViewVisibility(this.points, pointVisibility);

      if (perkVisibility == View.VISIBLE) this.perks.setText(rootView.getContext()
            .getString(R.string.perks_formatted, perks));
   }
}
