package com.worldventures.dreamtrips.modules.settings.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.model.FlagSettings;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_settings_flag)
public class SettingsFlagCell extends AbstractDelegateCell<FlagSettings, CellDelegate<FlagSettings>> {

    @InjectView(R.id.settings_title)
    TextView settingsTitle;
    @InjectView(R.id.flag_checkbox)
    CheckBox flag;

    public SettingsFlagCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        settingsTitle.setText(getModelObject().getName());
        flag.setChecked(getModelObject().getValue());
    }

    @Override
    public void prepareForReuse() {

    }
}
