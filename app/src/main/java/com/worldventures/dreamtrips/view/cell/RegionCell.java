package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.utils.busevents.RegionSetChangedEvent;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Edward on 22.01.15.
 */
@Layout(R.layout.adapter_item_region)
public class RegionCell extends AbstractCell<Region> {

    @InjectView(R.id.textViewRegionName)
    TextView textViewName;
    @InjectView(R.id.checkBox)
    CheckBox checkBox;

    public RegionCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewName.setText(getModelObject().getName());
        checkBox.setChecked(getModelObject().isChecked());
    }

    @OnClick(R.id.checkBox)
    void checkBoxClick() {
        getModelObject().setChecked(checkBox.isChecked());
        getEventBus().post(new RegionSetChangedEvent());
    }

    @OnClick(R.id.textViewRegionName)
    void textViewRegionClick() {
        checkBox.setChecked(!checkBox.isChecked());
        getModelObject().setChecked(checkBox.isChecked());
        getEventBus().post(new RegionSetChangedEvent());
    }

    @Override
    public void prepareForReuse() {
        textViewName.setText("");
    }
}
