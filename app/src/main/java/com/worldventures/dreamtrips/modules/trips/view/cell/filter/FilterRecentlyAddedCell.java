package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.event.FilterShowRecentlyAddedEvent;
import com.worldventures.dreamtrips.modules.trips.model.FilterRecentlyAddedModel;

@Layout(R.layout.adapter_item_filter_one_checkbox)
public class FilterRecentlyAddedCell extends BoolCell<FilterRecentlyAddedModel> {
    public FilterRecentlyAddedCell(View view) {
        super(view);
    }

    @Override
    public int getTitle() {
        return R.string.filter_recently_added;
    }

    @Override
    public void sendEvent(boolean b) {
        getEventBus().post(new FilterShowRecentlyAddedEvent(b));
    }
}
