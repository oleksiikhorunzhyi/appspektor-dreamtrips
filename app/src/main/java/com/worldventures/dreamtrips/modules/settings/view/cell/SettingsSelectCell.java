package com.worldventures.dreamtrips.modules.settings.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.model.SelectSettings;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_settings_select)
public class SettingsSelectCell extends AbstractDelegateCell<SelectSettings, CellDelegate<SelectSettings>> {

    @InjectView(R.id.settings_title)
    TextView settingsTitle;
    @InjectView(R.id.settings_value)
    TextView settingsValue;

    private SettingsManager settingsManager;

    public SettingsSelectCell(View view) {
        super(view);
        settingsManager = new SettingsManager();
    }

    @Override
    protected void syncUIStateWithModel() {
        settingsTitle.setText(settingsManager.getLocalizedTitleResource(getModelObject().getName()));
        settingsValue.setText(settingsManager.getLocalizedOptionResource(getModelObject().getValue()));
    }

    @Override
    public void prepareForReuse() {

    }
}
