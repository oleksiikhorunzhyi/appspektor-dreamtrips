package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.properratingbar.ProperRatingBar;

@Layout(R.layout.adapter_item_dtl_merchant_expandable)
public class DtlMerchantExpandableCell
        extends AbstractDelegateCell<DtlMerchant, DtlMerchantCellDelegate> {

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

    private final LayoutInflater inflater;

    public DtlMerchantExpandableCell(View view) {
        super(view);
        inflater = (LayoutInflater) view.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void syncUIStateWithModel() {
        merchantName.setText(getModelObject().getDisplayName());
        pricing.setRating(getModelObject().getBudget());
        //
        setImage(getModelObject().getImages());
        setDistance();
        setCategories();
        setOperationalStatus();
        setOffersSection();
        setToggleView();
        // TODO :: 5/20/16 Below is a dirtiest hack of all. If this is not re-done to proper
        // TODO :: expandable implementation until release or at most latest - until next release
        // TODO :: please tear off my hands
        setExpandedArea();
    }

    private void setImage(List<DtlMerchantMedia> mediaList) {
        DtlMerchantMedia media = Queryable.from(mediaList).firstOrDefault();
        if (media == null) return;
        //
        merchantCoverImage.setImageUrl(media.getImagePath());
    }

    private void setDistance() {
        Resources res = merchantDistance.getResources();
        ViewUtils.setTextOrHideView(merchantDistance, res.getString(
                R.string.distance_caption_format,
                getModelObject().getDistance(),
                res.getString(getModelObject().getDistanceType() == DistanceType.MILES ?
                        R.string.mi : R.string.km)));
    }

    private void setCategories() {
        String categoriesString = DtlMerchantHelper.getCategories(getModelObject());
        ViewUtils.setTextOrHideView(merchantCategories, categoriesString);
    }

    private void setOperationalStatus() {
        if (getModelObject().hasPoints() &&
                getModelObject().getOperationDays() != null && !getModelObject().getOperationDays().isEmpty()) {
            ViewUtils.setViewVisibility(merchantOperationalStatus, View.VISIBLE);
            this.merchantOperationalStatus.setText(DtlMerchantHelper.getOperationalTime(itemView.getContext(), getModelObject(), false));
        } else ViewUtils.setViewVisibility(merchantOperationalStatus, View.INVISIBLE);
    }

    private void setOffersSection() {
        if (getModelObject().hasNoOffers()) ViewUtils.setViewVisibility(offersContainer, View.GONE);
        else {
            ViewUtils.setViewVisibility(offersContainer, View.VISIBLE);
            int perksNumber = Queryable.from(getModelObject().getOffers()).count(DtlOffer::isPerk);
            setOfferBadges(perksNumber, getModelObject().getOffers().size() - perksNumber);
        }
    }

    private void setOfferBadges(int perks, int points) {
        int perkVisibility = getModelObject().isExpanded()
                ? View.GONE : perks > 0 ? View.VISIBLE : View.GONE;
        int pointVisibility = getModelObject().isExpanded()
                ? View.GONE : points > 0 ? View.VISIBLE : View.GONE;

        ViewUtils.setViewVisibility(this.perks, perkVisibility);
        ViewUtils.setViewVisibility(this.points, pointVisibility);

        if (perkVisibility == View.VISIBLE)
            this.perks.setText(itemView.getContext().getString(R.string.perks_formatted, perks));
    }

    private void setToggleView() {
        int toggleDrawable = getModelObject().isExpanded() ?
                R.drawable.ic_arrow_up_grey : R.drawable.ic_arrow_down_grey;
        perkToggleImage.setBackgroundResource(toggleDrawable);
        //
        ViewUtils.setViewVisibility(perkToggleText, getModelObject().isExpanded()
                ? View.VISIBLE : View.GONE);
    }

    /**
     * Very dirty. Needs proper expandable implementation
     * TODO :: 5/20/16
     */
    private void setExpandedArea() {
        if (getModelObject().isExpanded()) {
            expandedContainer.setVisibility(View.VISIBLE);
            expandedContainer.removeAllViews();
            Queryable.from(getModelObject().getOffers()).forEachR(offer -> {
                if (offer.isPerk()) bindPerkCell(offer, expandedContainer);
                else bindPointsCell(offer, expandedContainer);
            });
        } else {
            expandedContainer.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener offerClickedListener =
            v -> cellDelegate.onOfferClick(getModelObject(), (DtlOffer) v.getTag());

    @OnClick(R.id.offers_container)
    void togglExpandClicked() {
        getModelObject().toggleExpanded();
        cellDelegate.onExpandedToggle(getAdapterPosition());
    }

    @OnClick(R.id.merchantCellBodyLayout)
    void merchantClicked() {
        cellDelegate.onCellClicked(getModelObject());
    }

    private void bindPointsCell(DtlOffer offer, ViewGroup container) {
        View cellView = inflater.inflate(R.layout.adapter_item_offer_points, container, false);
        cellView.setTag(offer);
        cellView.setOnClickListener(offerClickedListener);
        container.addView(cellView);
    }

    private void bindPerkCell(DtlOffer offer, ViewGroup container) {
        View cellView = inflater.inflate(R.layout.adapter_item_offer_perk, container, false);
        cellView.setTag(offer);
        cellView.setOnClickListener(offerClickedListener);
        ImageryDraweeView image = ButterKnife.<ImageryDraweeView>findById(cellView, R.id.perk_logo);
        TextView title = ButterKnife.<TextView>findById(cellView, R.id.perks_description);
        TextView operationDaysCaption =
                ButterKnife.<TextView>findById(cellView, R.id.perks_operation_days);
        AppCompatTextView expirationBar =
                ButterKnife.<AppCompatTextView>findById(cellView, R.id.expirationBar);
        //
        DtlOfferMedia media = Queryable.from(offer.getImages()).firstOrDefault(); // image
        if (media != null) {
            image.setImageUrl(media.getImagePath());
        }
        //
        if (DtlMerchantHelper.isOfferExpiringSoon(offer)) { // expiration bar
            expirationBar.setVisibility(View.VISIBLE);
            expirationBar.setText(DtlMerchantHelper.
                    getOfferExpiringCaption(itemView.getContext(), offer));
        } else {
            expirationBar.setVisibility(View.GONE);
        }
        //
        if (offer.getDescription() != null) // description
            title.setText(offer.getDescription());
        //
        List<OperationDay> operationDays = offer.getOperationDays(); // operation days
        if (operationDays == null) return;
        String concatDays =
                DateTimeUtils.concatOperationDays(itemView.getResources(), operationDays);
        operationDaysCaption.setText(concatDays);
        //
        container.addView(cellView);
    }

    @Override
    public void prepareForReuse() {
    }
}
