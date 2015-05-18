package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.CheckBoxAllRegionsPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.ToggleRegionVisibilityEvent;
import com.worldventures.dreamtrips.modules.trips.model.RegionHeaderModel;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_region_header)
public class HeaderRegionCell extends AbstractCell<RegionHeaderModel> {

    @InjectView(R.id.checkBoxSelectAllRegion)
    protected CheckBox checkBoxSelectAll;

    public HeaderRegionCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        checkBoxSelectAll.setChecked(getModelObject().isChecked());
    }

    @OnClick(R.id.checkBoxSelectAllRegion)
    void checkBoxClicked() {
        getModelObject().setChecked(checkBoxSelectAll.isChecked());
        getEventBus().post(new CheckBoxAllRegionsPressedEvent(checkBoxSelectAll.isChecked()));
    }

    @OnClick(R.id.textViewSelectAllRegion)
    void checkBoxTextViewClicked() {
        checkBoxSelectAll.setChecked(!checkBoxSelectAll.isChecked());
        getModelObject().setChecked(checkBoxSelectAll.isChecked());
        getEventBus().post(new CheckBoxAllRegionsPressedEvent(checkBoxSelectAll.isChecked()));
    }

    @OnClick(R.id.listHeader)
    void toggleVisibility() {
        getEventBus().post(new ToggleRegionVisibilityEvent());
    }

    @Override
    public void prepareForReuse() {
        //nothing to do here
    }
}

