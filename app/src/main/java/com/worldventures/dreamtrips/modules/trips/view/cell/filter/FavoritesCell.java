package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.event.FilterShowFavoritesEvent;
import com.worldventures.dreamtrips.modules.trips.model.FilterFavoriteModel;

@Layout(R.layout.adapter_item_filter_one_checkbox)
public class FavoritesCell extends BoolCell<FilterFavoriteModel> {

    public FavoritesCell(View view) {
        super(view);
    }

    @Override
    public int getTitle() {
        return R.string.filters_show_favorite;
    }

    @Override
    public void sendEvent(boolean b) {
        getEventBus().post(new FilterShowFavoritesEvent(b));
    }
}
