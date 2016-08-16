package com.worldventures.dreamtrips.modules.settings.view.fragment;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.model.SelectSetting;
import com.worldventures.dreamtrips.modules.settings.view.cell.delegate.SettingsSelectCellDelegate;

@Layout(R.layout.fragment_settings)
public class GeneralSettingsFragment extends SettingsFragment implements SettingsSelectCellDelegate {

    @Override
    protected void registerCells() {
        super.registerCells();
        adapter.registerDelegate(SelectSetting.class, this);
    }

    @Override
    public void onValueSelected() {
        getPresenter().applyChanges();
    }

    @Override
    public void onAppliedChanges() {
        //nothing
    }

    @Override
    public void onCellClicked(SelectSetting model) {

    }
}
