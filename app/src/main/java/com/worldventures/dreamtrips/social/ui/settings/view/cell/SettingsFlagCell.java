package com.worldventures.dreamtrips.social.ui.settings.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.worldventures.core.modules.settings.model.FlagSetting;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.settings.util.SettingsManager;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_settings_flag)
public class SettingsFlagCell extends BaseAbstractDelegateCell<FlagSetting, CellDelegate<FlagSetting>> {

   @InjectView(R.id.settings_title) TextView settingsTitle;
   @InjectView(R.id.flag_checkbox) CheckBox flag;

   public SettingsFlagCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      settingsTitle.setText(SettingsManager.getLocalizedTitleResource(getModelObject().getName()));
      flag.setChecked(getModelObject().getValue());
   }

   @OnClick(R.id.main_view)
   void onCellClicked() {
      getModelObject().setValue(!getModelObject().getValue());
      flag.setChecked(getModelObject().getValue());
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
