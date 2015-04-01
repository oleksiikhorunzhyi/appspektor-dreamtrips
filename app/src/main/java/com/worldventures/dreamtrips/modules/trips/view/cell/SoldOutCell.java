package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.view.View;
import android.widget.CheckBox;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.SoldOutEvent;
import com.worldventures.dreamtrips.modules.trips.model.SoldOutModel;

import butterknife.InjectView;

/**
 * Edward on 23.01.15.
 * cell for show sold out filter
 */
@Layout(R.layout.adapter_item_show_sold_out)
public class SoldOutCell extends AbstractCell<SoldOutModel> {

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
            getEventBus().post(new SoldOutEvent(isChecked));
        });
    }

    @Override
    public void prepareForReuse() {

    }
}
