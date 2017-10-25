package com.worldventures.dreamtrips.social.ui.settings.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.modules.settings.model.SettingsGroup;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_settings)
public class SettingsGroupCell extends BaseAbstractDelegateCell<SettingsGroup, CellDelegate<SettingsGroup>> {

   @InjectView(R.id.setting_title) TextView title;

   public SettingsGroupCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      title.setText(getModelObject().getTitle());
   }

   @OnClick(R.id.settings_holder)
   void onSettingsClicked() {
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
