package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.BoolFilter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_filter_one_checkbox)
public abstract class BoolCell<T extends BoolFilter, D extends CellDelegate<T>> extends AbstractDelegateCell<T, D> {

    @InjectView(R.id.checkFavorites) CheckBox checkFavorites;
    @InjectView(R.id.textViewFavorite) TextView title;

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

    public abstract int getTitle();


    public abstract void sendEvent(boolean b);
}
