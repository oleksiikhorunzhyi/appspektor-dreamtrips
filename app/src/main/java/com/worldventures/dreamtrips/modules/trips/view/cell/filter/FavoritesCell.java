package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.event.FilterShowFavoritesEvent;
import com.worldventures.dreamtrips.modules.trips.model.FilterFavoriteModel;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_show_favorites)
public class FavoritesCell extends AbstractCell<FilterFavoriteModel> {

    @InjectView(R.id.checkFavorites)
    protected CheckBox checkFavorites;

    public FavoritesCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        checkFavorites.setChecked(getModelObject().isShowFavorites());
        checkFavorites.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getModelObject().setShowFavorites(isChecked);
            getEventBus().post(new FilterShowFavoritesEvent(isChecked));
        });
    }

    @OnClick(R.id.textViewFavorite)
    void checkBoxTextViewClicked() {
        checkFavorites.setChecked(!checkFavorites.isChecked());
        getModelObject().setShowFavorites(checkFavorites.isChecked());
    }

    @Override
    public void prepareForReuse() {
        //nothing to do here
    }
}
