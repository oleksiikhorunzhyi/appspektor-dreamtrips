package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.ThemeSetChangedEvent;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_region)
public class ActivityCell extends AbstractCell<ActivityModel> {

    @InjectView(R.id.cell)
    LinearLayout cell;
    @InjectView(R.id.textViewRegionName)
    TextView textViewName;
    @InjectView(R.id.checkBox)
    CheckBox checkBox;
    @Inject
    Context context;

    public ActivityCell(View view) {
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
        getEventBus().post(new ThemeSetChangedEvent());
    }

    @OnClick(R.id.textViewRegionName)
    void textViewRegionClick() {
        checkBox.setChecked(!checkBox.isChecked());
        getModelObject().setChecked(checkBox.isChecked());
        getEventBus().post(new ThemeSetChangedEvent());
    }

    @Override
    public void prepareForReuse() {
        textViewName.setText("");
    }
}
