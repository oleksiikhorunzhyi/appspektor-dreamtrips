package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.CheckBoxAllThemePressedEvent;
import com.worldventures.dreamtrips.modules.trips.event.FilterShowSoldOutEvent;
import com.worldventures.dreamtrips.modules.trips.model.FilterSoldOutModel;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_show_sold_out)
public class SoldOutCell extends AbstractCell<FilterSoldOutModel> {

    @InjectView(R.id.checkBoxSold)
    protected CheckBox checkBoxSold;

    public SoldOutCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        checkBoxSold.setChecked(getModelObject().isShowSoldOut());
        checkBoxSold.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getModelObject().setShowSoldOut(isChecked);
            getEventBus().post(new FilterShowSoldOutEvent(isChecked));
        });
    }

    @OnClick(R.id.textViewSoldOut)
    void checkBoxTextViewClicked() {
        checkBoxSold.setChecked(!checkBoxSold.isChecked());
        getModelObject().setShowSoldOut(checkBoxSold.isChecked());
    }

    @Override
    public void prepareForReuse() {
        //nothing to do here
    }
}
