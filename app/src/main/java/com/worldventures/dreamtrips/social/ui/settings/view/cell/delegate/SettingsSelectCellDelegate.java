package com.worldventures.dreamtrips.social.ui.settings.view.cell.delegate;

import com.worldventures.core.modules.settings.model.SelectSetting;
import com.worldventures.core.ui.view.cell.CellDelegate;

public interface SettingsSelectCellDelegate extends CellDelegate<SelectSetting> {

   void onValueSelected();
}
