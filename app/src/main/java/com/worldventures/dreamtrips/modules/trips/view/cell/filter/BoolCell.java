package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.event.FilterShowFavoritesEvent;
import com.worldventures.dreamtrips.modules.trips.model.BoolFilter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_filter_one_checkbox)
public abstract class BoolCell<T extends BoolFilter> extends AbstractCell<T> {

    @InjectView(R.id.checkFavorites)
    protected CheckBox checkFavorites;

    @InjectView(R.id.textViewFavorite)
    protected TextView title;

    public BoolCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        title.setText(getTitle());
        checkFavorites.setChecked(getModelObject().isActive());
        checkFavorites.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getModelObject().setActive(isChecked);
            sendEvent(isChecked);
        });
    }

    @OnClick(R.id.textViewFavorite)
    void checkBoxTextViewClicked() {
        checkFavorites.setChecked(!checkFavorites.isChecked());
        getModelObject().setActive(checkFavorites.isChecked());
    }

    @Override
    public void prepareForReuse() {
        //nothing to do here
    }

    public abstract int getTitle();


    public abstract void sendEvent(boolean b);
}
