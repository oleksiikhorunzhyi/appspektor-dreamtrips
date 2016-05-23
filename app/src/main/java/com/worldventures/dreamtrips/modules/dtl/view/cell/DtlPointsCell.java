package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsScreenImpl;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_offer_points)
public class DtlPointsCell extends AbstractDelegateCell<DtlOffer, DtlMerchantsScreenImpl.OfferCLickDelegate> {

    public DtlPointsCell(View view) {
        super(view);
    }

    @OnClick(R.id.points_view)
    protected void onPerkClick() {
        cellDelegate.onCellClicked(getModelObject());
    }

    @Override
    protected void syncUIStateWithModel() {
    }

    @Override
    public void prepareForReuse() {
    }
}
