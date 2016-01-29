package com.worldventures.dreamtrips.modules.settings.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.model.FlagSettings;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_settings_flag)
public class SettingsFlagCell extends AbstractDelegateCell<FlagSettings, CellDelegate<FlagSettings>> {

    @InjectView(R.id.settings_title)
    TextView settingsTitle;
    @InjectView(R.id.flag_checkbox)
    CheckBox flag;

    private SettingsManager settingsManager;

    public SettingsFlagCell(View view) {
        super(view);
        settingsManager = new SettingsManager();
    }

    @Override
    protected void syncUIStateWithModel() {
        settingsTitle.setText(settingsManager.getLocalizedTitleResource(getModelObject().getName()));
        flag.setChecked(getModelObject().getValue());
    }

    @OnClick(R.id.main_view)
    void onCellClicked() {
        getModelObject().setValue(!getModelObject().getValue());
        flag.setChecked(getModelObject().getValue());
        cellDelegate.onCellClicked(getModelObject());
    }

    @Override
    public void prepareForReuse() {

    }
}
