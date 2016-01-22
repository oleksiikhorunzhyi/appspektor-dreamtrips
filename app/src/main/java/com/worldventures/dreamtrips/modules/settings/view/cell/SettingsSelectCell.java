package com.worldventures.dreamtrips.modules.settings.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.model.SelectSettings;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_settings_select)
public class SettingsSelectCell extends AbstractDelegateCell<SelectSettings, CellDelegate<SelectSettings>> {

    @InjectView(R.id.settings_title)
    TextView settingsTitle;
    @InjectView(R.id.settings_value)
    TextView settingsValue;

    public SettingsSelectCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        settingsTitle.setText(getModelObject().getName());
        settingsValue.setText(getModelObject().getValue());
    }

    @Override
    public void prepareForReuse() {

    }
}
