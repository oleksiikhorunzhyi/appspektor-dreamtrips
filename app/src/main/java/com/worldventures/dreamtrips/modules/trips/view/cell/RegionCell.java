package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.RegionSetChangedEvent;
import com.worldventures.dreamtrips.modules.trips.model.Region;

import javax.inject.Inject;

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
    @Inject
    Context context;
    @InjectView(R.id.cell)
    LinearLayout cell;

    public RegionCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewName.setText(getModelObject().getName());
        textViewName.setTextColor(getModelObject().isChecked() ?
                context.getResources().getColor(R.color.textViewFilterEnabled) :
                context.getResources().getColor(R.color.textViewFilterDisabled));
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
