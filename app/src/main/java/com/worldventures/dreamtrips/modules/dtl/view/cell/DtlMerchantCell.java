package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;
import com.worldventures.dreamtrips.core.selectable.SelectableDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.PlaceClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceSingleImageDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_place)
public class DtlMerchantCell extends AbstractCell<DtlMerchant> implements SelectableCell {

    DtlPlaceCommonDataInflater commonDataInflater;
    DtlPlaceInfoInflater categoryDataInflater;

    private SelectableDelegate selectableDelegate;

    public DtlMerchantCell(View view) {
        super(view);
        DtlPlaceHelper helper = new DtlPlaceHelper(view.getContext());
        commonDataInflater = new DtlPlaceSingleImageDataInflater(helper);
        categoryDataInflater = new DtlPlaceInfoInflater(helper);
        commonDataInflater.setView(view);
        categoryDataInflater.setView(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        commonDataInflater.apply(getModelObject());
        categoryDataInflater.apply(getModelObject());
        itemView.setSelected(selectableDelegate.isSelected(getAdapterPosition()));
    }

    @OnClick(R.id.place_details_root)
    void placeClicked() {
        if (!selectableDelegate.isSelected(getAdapterPosition()))
            selectableDelegate.toggleSelection(getAdapterPosition());
        //
        getEventBus().post(new PlaceClickedEvent(getModelObject().getId()));
    }

    @Override
    public void prepareForReuse() {
        //
    }

    @Override
    public void setSelectableDelegate(SelectableDelegate selectableDelegate) {
        this.selectableDelegate = selectableDelegate;
    }
}
