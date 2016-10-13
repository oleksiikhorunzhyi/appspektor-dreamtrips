package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;
import com.worldventures.dreamtrips.core.selectable.SelectableDelegate;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.MerchantCellDelegate;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.properratingbar.ProperRatingBar;
import rx.Observable;

@Layout(R.layout.adapter_item_dtl_merchant_expandable)
public class DtlMerchantExpandableCell extends AbstractDelegateCell<ImmutableThinMerchant, MerchantCellDelegate> implements SelectableCell {

   @InjectView(R.id.merchantCoverImage) ImageryDraweeView merchantCoverImage;
   @InjectView(R.id.merchantPricing) ProperRatingBar pricing;
   @InjectView(R.id.merchantName) TextView merchantName;
   @InjectView(R.id.merchantCategories) TextView merchantCategories;
   @InjectView(R.id.merchantOpenClosedStatus) TextView merchantOperationalStatus;
   @InjectView(R.id.view_points) TextView points;
   @InjectView(R.id.view_perks) TextView perks;
   @InjectView(R.id.merchantDistance) TextView merchantDistance;
   @InjectView(R.id.offers_container) View offersContainer;
   @InjectView(R.id.perk_toggle_view) ImageView perkToggleImage;
   @InjectView(R.id.perk_toggle_label) TextView perkToggleText;
   @InjectView(R.id.expandedContainer) ViewGroup expandedContainer;

   @Inject LocaleHelper localeHelper;

   private SelectableDelegate selectableDelegate;
   private DistanceType distanceType;
   private boolean expanded;

   public DtlMerchantExpandableCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      merchantName.setText(getModelObject().displayName());
      pricing.setRating(getModelObject().budget());
      //
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
      if (media == null) return;
      //
      merchantCoverImage.setImageUrl(media.getImagePath());
   }

   private void setDistance() {
      Resources res = merchantDistance.getResources();
      ViewUtils.setTextOrHideView(merchantDistance, getModelObject().asMerchantAttributes().provideFormattedDistance(res, distanceType));
   }

   private void setCategories() {
      String categoriesString = getModelObject().asMerchantAttributes().provideFormattedCategories();
      ViewUtils.setTextOrHideView(merchantCategories, categoriesString);
   }

   private void setOperationalStatus() {
      if (getModelObject().asMerchantAttributes().hasPoints() && getModelObject().asMerchantAttributes().hasOperationDays()) {
         ViewUtils.setViewVisibility(merchantOperationalStatus, View.VISIBLE);
         Observable.fromCallable(() -> getModelObject().asMerchantAttributes().provideFormattedOperationalTime(itemView.getContext(), false))
               .compose(RxLifecycle.bindView(itemView))
               .subscribe(merchantOperationalStatus::setText, ex -> merchantOperationalStatus.setVisibility(View.GONE));
      } else ViewUtils.setViewVisibility(merchantOperationalStatus, View.INVISIBLE);
   }

   private void setOffersSection() {
      if (!getModelObject().asMerchantAttributes().hasOffers()) ViewUtils.setViewVisibility(offersContainer, View.GONE);
      else {
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

      if (perkVisibility == View.VISIBLE) this.perks.setText(itemView.getContext()
            .getString(R.string.perks_formatted, perks));
   }

   private void setToggleView() {
      int toggleDrawable = expanded ? R.drawable.ic_arrow_up_grey : R.drawable.ic_arrow_down_grey;
      perkToggleImage.setBackgroundResource(toggleDrawable);
      //
      ViewUtils.setViewVisibility(perkToggleText, expanded ? View.VISIBLE : View.GONE);
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
            if (offer.type() == OfferType.PERK) bindPerkCell(offer, expandedContainer);
            else bindPointsCell(offer, expandedContainer);
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
      View cellView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.adapter_item_offer_points, container, false);
      cellView.setTag(offer);
      cellView.setOnClickListener(v -> cellDelegate.onOfferClick(getModelObject(), (Offer) v.getTag()));
      setSelection(cellView);
      container.addView(cellView);
   }

   private void bindPerkCell(Offer offer, ViewGroup container) {
      View cellView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.adapter_item_offer_perk, container, false);
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
               getOfferExpiringCaption(itemView.getContext(), offer, localeHelper.getDefaultLocale()));
      } else ViewUtils.setViewVisibility(View.GONE, expirationBar);
      //
      title.setText(offer.title()); // description
      //
      List<OperationDay> operationDays = offer.operationDays(); // operation days
      if (operationDays == null) return;
      String concatDays = DateTimeUtils.concatOperationDays(itemView.getResources(), operationDays);
      operationDaysCaption.setText(concatDays);
      //
      setSelection(cellView);
      container.addView(cellView);
   }

   @Override
   public void setSelectableDelegate(SelectableDelegate selectableDelegate) {
      this.selectableDelegate = selectableDelegate;
   }
}
