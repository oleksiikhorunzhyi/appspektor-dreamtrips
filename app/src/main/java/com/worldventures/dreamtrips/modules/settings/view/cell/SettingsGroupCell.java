package com.worldventures.dreamtrips.modules.settings.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_settings)
public class SettingsGroupCell extends AbstractDelegateCell<SettingsGroup, CellDelegate<SettingsGroup>> {

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
}
