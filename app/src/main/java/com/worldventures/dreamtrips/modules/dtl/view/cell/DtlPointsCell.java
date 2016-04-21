package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPointsData;

@Layout(R.layout.adapter_item_offer_points)
public class DtlPointsCell extends AbstractCell<DtlOfferPointsData>{

    public DtlPointsCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {

    }

    @Override
    public void prepareForReuse() {

    }
}
