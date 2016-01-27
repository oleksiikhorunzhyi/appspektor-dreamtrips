package com.worldventures.dreamtrips.modules.settings.view.cell;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.dialog.SelectDialog;
import com.worldventures.dreamtrips.modules.settings.model.SelectSettings;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;
import com.worldventures.dreamtrips.modules.settings.view.cell.delegate.SettingsSelectCellDelegate;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_settings_select)
public class SettingsSelectCell extends AbstractDelegateCell<SelectSettings, SettingsSelectCellDelegate> {

    @InjectView(R.id.settings_title)
    TextView settingsTitle;
    @InjectView(R.id.settings_value)
    TextView settingsValue;

    @Inject
    FragmentManager fragmentManager;

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

    @OnClick({R.id.settings_item_holder})
    public void onItemClick(View view) {
        showSingleChoiceDialog(view.getContext());
    }

    private void showSingleChoiceDialog(Context context) {
        List<String> options = getModelObject().getOptions();
        int optionsSize = options.size();
        int titleId = settingsManager.getLocalizedTitleResource(getModelObject().getName());
        String[] items = new String[optionsSize];
        int selectedPosition = -1;

        for (int i = 0; i < optionsSize; i++) {
            items[i] = context.getResources().getString(settingsManager.getLocalizedOptionResource(options.get(i)));
            if (options.get(i).equals(getModelObject().getValue())) {
                selectedPosition = i;
            }
        }

        SelectDialog dialog = SelectDialog.newInstance(titleId, items, selectedPosition);
        dialog.setSelectionListener((position, value) -> {
            getModelObject().setValue(getModelObject().getOptions().get(position));
            cellDelegate.onValueSelected();
            syncUIStateWithModel();
        });
        dialog.show(fragmentManager);
    }

}
