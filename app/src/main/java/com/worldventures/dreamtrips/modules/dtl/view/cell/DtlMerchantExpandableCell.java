package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.expandable.GroupDelegateCell;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;

import java.util.Collections;
import java.util.List;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;
import rx.Observable;

@Layout(R.layout.adapter_item_dtl_merchant_expandable)
public class DtlMerchantExpandableCell extends GroupDelegateCell<DtlMerchant, DtlOfferData, CellDelegate<DtlMerchant>> {

    @InjectView(R.id.merchantCoverImage)
    ImageryDraweeView merchantCoverImage;
    @InjectView(R.id.merchantPricing)
    ProperRatingBar pricing;
    @InjectView(R.id.merchantName)
    TextView merchantName;
    @InjectView(R.id.merchantCategories)
    TextView merchantCategories;
    @InjectView(R.id.merchantOpenClosedStatus)
    TextView merchantOperationalStatus;
    @InjectView(R.id.view_points)
    TextView points;
    @InjectView(R.id.view_perks)
    TextView perks;
    @InjectView(R.id.merchantDistance)
    TextView merchantDistance;
    @InjectView(R.id.offers_container)
    View offersContainer;
    @InjectView(R.id.perk_toggle_view)
    ImageView perkToggleImage;
    @InjectView(R.id.perk_toggle_label)
    TextView perkToggleText;


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
        if (media == null) {
            return;
        }
        //
        merchantCoverImage.setImageUrl(media.getImagePath());
    }

    private void setDistance() {
        Resources res = merchantDistance.getResources();
        if (getModelObject().getDistance() != 0.0d) {
            merchantDistance.setVisibility(View.VISIBLE);
            merchantDistance.setText(res.getString(
                    R.string.distance_caption_format,
                    getModelObject().getDistance(),
                    res.getString(getModelObject().getDistanceType() == DistanceType.MILES ?
                            R.string.mi : R.string.km)));
        } else merchantDistance.setVisibility(View.GONE);
    }

    private void setCategories() {
        String categoriesString = DtlMerchantHelper.getCategories(getModelObject());
        if (!TextUtils.isEmpty(categoriesString)) {
            merchantCategories.setVisibility(View.VISIBLE);
            merchantCategories.setText(categoriesString);
        } else merchantCategories.setVisibility(View.GONE);
    }

    private void setOperationalStatus() {
        if (getModelObject().hasOffer(DtlOffer.TYPE_POINTS) &&
                getModelObject().getOperationDays() != null && !getModelObject().getOperationDays().isEmpty()) {
            merchantOperationalStatus.setVisibility(View.VISIBLE);
            merchantOperationalStatus.setText(DtlMerchantHelper.getOperationalTime(itemView.getContext(), getModelObject(), false));
        } else merchantOperationalStatus.setVisibility(View.INVISIBLE);
    }

    private void setOffersSection() {
        List<DtlOffer> offers = getModelObject().getOffers();
        if (!offers.isEmpty()) {
            offersContainer.setVisibility(View.VISIBLE);
            Observable.from(offers)
                    .compose(RxLifecycle.bindView(itemView))
                    .filter(offer -> offer.getType().equals(Offer.PERKS))
                    .count().subscribe(perks -> setOffersTypes(perks, offers.size() - perks));
        } else offersContainer.setVisibility(View.GONE);
    }

    private void setOffersTypes(int perks, int points) {
        if (perks > 0) {
            this.perks.setVisibility(View.VISIBLE);
            this.perks.setText(itemView.getContext().getString(R.string.perks_formatted, perks));
        } else this.perks.setVisibility(View.GONE);

        int visibility = points > 0 ? View.VISIBLE : View.GONE;
        this.points.setVisibility(visibility);
    }

    private void setToggleView() {
        int toggleDrawable = isExpanded() ? R.drawable.ic_arrow_up_grey : R.drawable.ic_arrow_down_grey;
        perkToggleImage.setBackgroundResource(toggleDrawable);
        perkToggleText.setVisibility(isExpanded() ? View.VISIBLE : View.GONE);
        perks.setVisibility(isExpanded() ? View.GONE : View.VISIBLE);
        points.setVisibility(isExpanded() ? View.GONE : View.VISIBLE);
    }

    @Override
    public List<DtlOfferData> getChildListCell() {
        List<DtlOfferData> childSet = Queryable.from(getModelObject().getOffers()).map(DtlOffer::getOffer).toList();
        Collections.sort(childSet);
        return childSet;
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
