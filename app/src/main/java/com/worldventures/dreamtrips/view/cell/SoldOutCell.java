package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.CheckBox;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.SoldOutModel;
import com.worldventures.dreamtrips.utils.busevents.SoldOutEvent;

import butterknife.InjectView;

/**
 * Created by Edward on 23.01.15.
 * cell for show sold out filter
 */
@Layout(R.layout.adapter_item_show_sold_out)
public class SoldOutCell extends AbstractCell<SoldOutModel> {

    @InjectView(R.id.checkBoxSold)
    CheckBox checkBoxSold;

    public SoldOutCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        checkBoxSold.setChecked(getModelObject().isShowSoldOut());
        checkBoxSold.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
                getModelObject().setShowSoldOut(isChecked);
                getEventBus().post(new SoldOutEvent(isChecked));
        });
    }

    @Override
    public void prepareForReuse() {

    }
}
