package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.expandable.GroupDelegateCell;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

import java.util.List;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

@Layout(R.layout.adapter_item_dtl_merchant_expandable)
public class DtlMerchantExpandableCell extends GroupDelegateCell<DtlMerchant, DtlOffer, CellDelegate<DtlMerchant>> {

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

    public DtlMerchantExpandableCell(View view) {
        super(view);
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
            setOffersTypes(perksNumber, getModelObject().getOffers().size() - perksNumber);
        }
    }

    private void setOffersTypes(int perks, int points) {
        int perkVisibility = isExpanded() ? View.GONE : perks > 0 ? View.VISIBLE : View.GONE;
        int pointVisibility = isExpanded() ? View.GONE : points > 0 ? View.VISIBLE : View.GONE;

        ViewUtils.setViewVisibility(this.perks, perkVisibility);
        ViewUtils.setViewVisibility(this.points, pointVisibility);

        if (perkVisibility == View.VISIBLE)
            this.perks.setText(itemView.getContext().getString(R.string.perks_formatted, perks));
    }

    private void setToggleView() {
        int toggleDrawable = isExpanded() ? R.drawable.ic_arrow_up_grey : R.drawable.ic_arrow_down_grey;
        perkToggleImage.setBackgroundResource(toggleDrawable);
        //
        ViewUtils.setViewVisibility(perkToggleText, isExpanded() ? View.VISIBLE : View.GONE);
    }

    @Override
    public List<DtlOffer> getChildListCell() {
        return getModelObject().getOffers();
    }

    @Override
    protected void onAction(ACTION action) {
        if (action == ACTION.CLICK) cellDelegate.onCellClicked(getModelObject());
    }

    @Override
    protected int getExpandItemViewId() {
        return R.id.offers_container;
    }

    @Override
    protected int getClickableItemViewId() {
        return R.id.root_container;
    }

    @Override
    public void prepareForReuse() {
    }
}
