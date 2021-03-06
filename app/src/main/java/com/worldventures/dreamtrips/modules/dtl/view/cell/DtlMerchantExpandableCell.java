package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.service.DeviceInfoProvider;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;
import com.worldventures.dreamtrips.core.selectable.SelectableDelegate;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationalHoursUtils;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.MerchantCellDelegate;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.ValidateReviewUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.properratingbar.ProperRatingBar;
import rx.Observable;

@Layout(R.layout.adapter_item_dtl_merchant_expandable)
public class DtlMerchantExpandableCell extends BaseAbstractDelegateCell<ImmutableThinMerchant, MerchantCellDelegate> implements SelectableCell {

   @InjectView(R.id.merchantCoverImage) ImageryDraweeView merchantCoverImage;
   @InjectView(R.id.merchantPricing) ProperRatingBar pricing;
   @InjectView(R.id.merchantName) TextView merchantName;
   @InjectView(R.id.merchantCategories) TextView merchantCategories;
   @InjectView(R.id.merchantOpenClosedStatus) TextView merchantOperationalStatus;
   @InjectView(R.id.view_points) TextView points;
   @InjectView(R.id.view_perks) TextView perks;
   @InjectView(R.id.view_pay_in_app) TextView payInApp;
   @InjectView(R.id.merchantDistance) TextView merchantDistance;
   @InjectView(R.id.offers_container) View offersContainer;
   // TODO Expandable cells functionality is disabled, delete it from the code completely
   @InjectView(R.id.perk_toggle_view) ImageView perkToggleImage;
   @InjectView(R.id.expandedContainer) ViewGroup expandedContainer;
   @InjectView(R.id.ratingBarReviews) RatingBar mRatingBar;
   @InjectView(R.id.text_view_rating) TextView textViewRating;
   @InjectView(R.id.layout_rating_reviews) LinearLayout layoutRatingsReview;

   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject DeviceInfoProvider deviceInfoProvider;

   private SelectableDelegate selectableDelegate;
   private DistanceType distanceType;
   private boolean expanded;

   @Inject SessionHolder appSessionHolder;

   public DtlMerchantExpandableCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      merchantName.setText(getModelObject().displayName());
      pricing.setRating(getModelObject().budget());

      setImage(getModelObject().images());
      setDistance();
      setCategories();
      setOperationalStatus();
      setOffersSection();
      setToggleView();
      setSelection(itemView);

      // TODO :: 5/20/16 Below is a dirtiest hack of all. If this is not re-done to proper
      // TODO :: expandable implementation until release or at most latest - until next release
      // TODO :: please tear off my hands
      setExpandedArea();
      ValidateReviewUtil.setUpRating(itemView.getContext(), getModelObject().reviewSummary(), mRatingBar, textViewRating);
   }

   private void setSelection(View view) {
      int colorResId = selectableDelegate.isSelected(getAdapterPosition()) ? R.color.dtl_selection_color : R.color.white;
      view.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), colorResId));
   }

   public void setDistanceType(DistanceType distanceType) {
      this.distanceType = distanceType;
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

   private void setImage(List<MerchantMedia> mediaList) {
      MerchantMedia media = Queryable.from(mediaList).firstOrDefault();
      if (media == null) {
         return;
      }
      //
      merchantCoverImage.setImageUrl(media.getImagePath());
   }

   private void setDistance() {
      Resources res = merchantDistance.getResources();
      ViewUtils.setTextOrHideView(merchantDistance, getModelObject().asMerchantAttributes()
            .provideFormattedDistance(res, distanceType));
   }

   private void setCategories() {
      String categoriesString = getModelObject().asMerchantAttributes().provideFormattedCategories();
      ViewUtils.setTextOrHideView(merchantCategories, categoriesString);
   }

   private void setOperationalStatus() {
      if (getModelObject().asMerchantAttributes().hasPoints() && getModelObject().asMerchantAttributes()
            .hasOperationDays()) {
         ViewUtils.setViewVisibility(merchantOperationalStatus, View.VISIBLE);
         Observable.fromCallable(() -> getModelObject().asMerchantAttributes()
               .provideFormattedOperationalTime(itemView.getContext(), false))
               .compose(RxLifecycleAndroid.bindView(itemView))
               .subscribe(merchantOperationalStatus::setText, ex -> merchantOperationalStatus.setVisibility(View.GONE));
      } else {
         ViewUtils.setViewVisibility(merchantOperationalStatus, View.INVISIBLE);
      }
   }

   private void setOffersSection() {
      if (!getModelObject().asMerchantAttributes().hasOffers()) {
         ViewUtils.setViewVisibility(offersContainer, View.GONE);
      } else {
         ViewUtils.setViewVisibility(offersContainer, View.VISIBLE);
         int perksNumber = getModelObject().asMerchantAttributes().offersCount(OfferType.PERK);
         setOfferBadges(perksNumber, getModelObject().offers().size() - perksNumber);
      }
   }

   private void setOfferBadges(int perks, int points) {
      int perkVisibility = perks > 0 ? View.VISIBLE : View.GONE;
      int pointVisibility = points > 0 ? View.VISIBLE : View.GONE;

      ViewUtils.setViewVisibility(this.perks, perkVisibility);
      ViewUtils.setViewVisibility(this.points, pointVisibility);

      if (getModelObject().useThrstFlow()) {
         ViewUtils.setViewVisibility(this.payInApp, View.VISIBLE);
         ViewUtils.setViewVisibility(this.points, View.GONE);
      } else {
         ViewUtils.setViewVisibility(this.payInApp, View.GONE);
         ViewUtils.setViewVisibility(this.points, View.VISIBLE);
      }

      if (perkVisibility == View.VISIBLE) {
         this.perks.setText(itemView.getContext().getString(R.string.perks_formatted, perks));
      }
   }

   private void setToggleView() {
      int toggleDrawable = expanded ? R.drawable.ic_arrow_up_grey : R.drawable.ic_arrow_down_grey;
      perkToggleImage.setBackgroundResource(toggleDrawable);
   }

   /**
    * Very dirty. Needs proper expandable implementation
    * TODO :: 5/20/16
    */
   private void setExpandedArea() {
      if (expanded) {
         expandedContainer.setVisibility(View.VISIBLE);
         expandedContainer.removeAllViews();
         Queryable.from(getModelObject().offers()).forEachR(offer -> {
            if (offer.type() == OfferType.PERK) {
               bindPerkCell(offer, expandedContainer);
            } else {
               bindPointsCell(offer, expandedContainer);
            }
         });
      } else {
         expandedContainer.setVisibility(View.GONE);
      }
   }

   @OnClick(R.id.offers_container)
   void togglExpandClicked() {
      cellDelegate.onToggleExpanded(!expanded, getModelObject());
   }

   @OnClick(R.id.merchantCellBodyLayout)
   void merchantClicked() {
      cellDelegate.onCellClicked(getModelObject());
   }

   private void bindPointsCell(Offer offer, ViewGroup container) {
      View cellView = LayoutInflater.from(itemView.getContext())
            .inflate(R.layout.adapter_item_offer_points, container, false);
      cellView.setTag(offer);
      cellView.setOnClickListener(v -> cellDelegate.onOfferClick(getModelObject(), (Offer) v.getTag()));
      setSelection(cellView);
      container.addView(cellView);
   }

   private void bindPerkCell(Offer offer, ViewGroup container) {
      View cellView = LayoutInflater.from(itemView.getContext())
            .inflate(R.layout.adapter_item_offer_perk, container, false);
      cellView.setTag(offer);
      cellView.setOnClickListener(v -> cellDelegate.onOfferClick(getModelObject(), (Offer) v.getTag()));
      ImageryDraweeView image = ButterKnife.<ImageryDraweeView>findById(cellView, R.id.perk_logo);
      TextView title = ButterKnife.<TextView>findById(cellView, R.id.perks_description);
      TextView operationDaysCaption = ButterKnife.<TextView>findById(cellView, R.id.perks_operation_days);
      AppCompatTextView expirationBar = ButterKnife.<AppCompatTextView>findById(cellView, R.id.expirationBar);
      //
      MerchantMedia media = Queryable.from(offer.images()).firstOrDefault(); // image
      if (media != null) {
         image.setImageUrl(media.getImagePath());
      }
      //
      if (MerchantHelper.isOfferExpiringSoon(offer)) { // expiration bar
         ViewUtils.setTextOrHideView(expirationBar, MerchantHelper.
               getOfferExpiringCaption(itemView.getContext(), offer, LocaleHelper.getDefaultLocale()));
      } else {
         ViewUtils.setViewVisibility(View.GONE, expirationBar);
      }
      //
      title.setText(offer.title()); // description
      //
      List<OperationDay> operationDays = offer.operationDays(); // operation days
      if (operationDays == null) {
         return;
      }
      String concatDays = OperationalHoursUtils.concatOperationDays(operationDays, itemView.getResources()
            .getString(R.string.everyday));
      operationDaysCaption.setText(concatDays);
      //
      setSelection(cellView);
      container.addView(cellView);
   }

   @Override
   public void setSelectableDelegate(SelectableDelegate selectableDelegate) {
      this.selectableDelegate = selectableDelegate;
   }

   @OnClick(R.id.layout_rating_reviews)
   void onClickRateView() {
      if (!deviceInfoProvider.isTablet()) {
         if (isReviewCached()) {
            if (userHasReviews()) {
               cellDelegate.sendToRatingReview(getModelObject());
            } else {
               cellDelegate.userHasPendingReview();
            }
         } else {
            if (!userHasPendingReview() || userHasReviews()) {
               cellDelegate.sendToRatingReview(getModelObject());
            } else {
               cellDelegate.userHasPendingReview();
            }
         }
      }
   }

   private User getUser() {
      return appSessionHolder.get().get().user();
   }

   private boolean isReviewCached() {
      return ReviewStorage.exists(itemView.getContext(), String.valueOf(getUser().getId()), getModelObject().id());
   }

   private boolean userHasReviews() {
      if (getModelObject().reviewSummary() == null) {
         return false;
      }

      try {
         return Integer.parseInt(getModelObject().reviewSummary().total()) > 0;
      } catch (NumberFormatException e) {
         // nothing to do, default value is false
      }

      return false;
   }

   private boolean userHasPendingReview() {
      return getModelObject().reviewSummary() != null && getModelObject().reviewSummary().userHasPendingReview();
   }
}
