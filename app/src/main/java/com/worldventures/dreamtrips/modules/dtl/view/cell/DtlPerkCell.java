package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPerkData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsScreenImpl;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_offer_perk)
public class DtlPerkCell extends AbstractDelegateCell<DtlOfferPerkData, DtlMerchantsScreenImpl.PerkDelegate> {

    @InjectView(R.id.perk_logo)
    SimpleDraweeView image;
    @InjectView(R.id.perks_description)
    TextView description;
    @InjectView(R.id.perks_operation_days)
    TextView operationDays;


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
        image.setImageURI(Uri.parse(media.getImagePath()));
    }

    private void bindDescription() {
        if (getModelObject().getDescription() != null) description.setText(getModelObject().getDescription());
    }

    private void bindOperationDays() {
        List<OperationDay> operationDays = getModelObject().getOperationDays();
        if (operationDays == null) return;
        //
        String concatDays = DateTimeUtils.concatOperationDays(itemView.getResources(), operationDays);
        this.operationDays.setText(concatDays);
    }
}
