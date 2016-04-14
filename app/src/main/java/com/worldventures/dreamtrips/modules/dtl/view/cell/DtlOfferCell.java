package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_offer)
public class DtlOfferCell extends AbstractCell<DtlOffer> {

    @InjectView(android.R.id.text1)
    TextView description;

    public DtlOfferCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        description.setText(getModelObject().getType());
    }

    @Override
    public void prepareForReuse() {

    }
}
