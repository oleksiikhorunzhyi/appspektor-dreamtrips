package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.event.FilterShowSoldOutEvent;
import com.worldventures.dreamtrips.modules.trips.model.FilterSoldOutModel;

@Layout(R.layout.adapter_item_filter_one_checkbox)
public class SoldOutCell extends BoolCell<FilterSoldOutModel> {

    public SoldOutCell(View view) {
        super(view);
    }

    @Override
    public int getTitle() {
        return R.string.filter_show_sold_out;
    }

    @Override
    public void sendEvent(boolean b) {
        getEventBus().post(new FilterShowSoldOutEvent(b));
    }
}
