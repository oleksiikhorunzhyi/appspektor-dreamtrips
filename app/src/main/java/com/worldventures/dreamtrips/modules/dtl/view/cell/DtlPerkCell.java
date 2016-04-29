package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPerkData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsScreenImpl;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_offer_perk)
public class DtlPerkCell
        extends AbstractDelegateCell<DtlOfferPerkData, DtlMerchantsScreenImpl.PerkDelegate> {

    @InjectView(R.id.perk_logo) ImageryDraweeView image;
    @InjectView(R.id.perks_description) TextView title;
    @InjectView(R.id.perks_operation_days) TextView operationDays;
    @InjectView(R.id.expirationBarLayout) ViewGroup expirationBarLayout;
    @InjectView(R.id.expirationBarCaption) AppCompatTextView expirationBarCaption;

    public DtlPerkCell(View view) {
        super(view);
    }

    @OnClick(R.id.perks_view)
    protected void onPerkClick(){
        cellDelegate.onCellClicked(getModelObject());
    }

    @Override
    protected void syncUIStateWithModel() {
        bindImage();
        bindExpirationBar();
        bindDescription();
        bindOperationDays();
    }

    @Override
    public void prepareForReuse() {
    }

    private void bindImage() {
        DtlOfferMedia media = Queryable.from(getModelObject().getImages()).firstOrDefault();
        if (media == null) return;
        //
        image.setImageUrl(media.getImagePath());
    }

    private void bindExpirationBar() {
        if (DtlMerchantHelper.isOfferExpiringSoon(getModelObject())) {
            expirationBarLayout.setVisibility(View.VISIBLE);
            expirationBarCaption.setText(DtlMerchantHelper.
                    getOfferExpiringCaption(itemView.getContext(), getModelObject()));
        } else {
            expirationBarLayout.setVisibility(View.GONE);
        }
    }

    private void bindDescription() {
        if (getModelObject().getDescription() != null)
            title.setText(getModelObject().getDescription());
    }

    private void bindOperationDays() {
        List<OperationDay> operationDays = getModelObject().getOperationDays();
        if (operationDays == null) return;
        //
        String concatDays =
                DateTimeUtils.concatOperationDays(itemView.getResources(), operationDays);
        this.operationDays.setText(concatDays);
    }
}
