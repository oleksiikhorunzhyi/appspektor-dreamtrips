package com.worldventures.dreamtrips.modules.settings.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.settings.model.SelectSetting;

public interface SettingsSelectCellDelegate extends CellDelegate<SelectSetting> {

   void onValueSelected();
}
