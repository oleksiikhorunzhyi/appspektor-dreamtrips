package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.event.PlaceClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceSingleImageDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_place)
public class DtlPlaceCell extends AbstractCell<DtlPlace> {

    DtlPlaceCommonDataInflater commonDataInflater;
    DtlPlaceInfoInflater categoryDataInflater;

    public DtlPlaceCell(View view) {
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
    }

    @OnClick(R.id.place_details_root)
    void placeClicked() {
        getEventBus().post(new PlaceClickedEvent(getModelObject()));
    }

    @Override
    public void prepareForReuse() {
        //
    }
}
