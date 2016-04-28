package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPointsData;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsScreenImpl;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_offer_points)
public class DtlPointsCell extends AbstractDelegateCell<DtlOfferPointsData, DtlMerchantsScreenImpl.PointsDelegate> {

    @InjectView(R.id.pointsDescription) TextView title;

    public DtlPointsCell(View view) {
        super(view);
    }

    @OnClick(R.id.points_view)
    protected void onPerkClick() {
        cellDelegate.onCellClicked(getModelObject());
    }

    @Override
    protected void syncUIStateWithModel() {
        bindDescription();
    }

    private void bindDescription() {
        if (getModelObject().getTitle() != null) title.setText(getModelObject().getDescription());
    }


    @Override
    public void prepareForReuse() {

    }
}
