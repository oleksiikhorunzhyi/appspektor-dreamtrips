package com.worldventures.dreamtrips.modules.settings.view.cell;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.dialog.SelectDialog;
import com.worldventures.dreamtrips.modules.settings.model.SelectSetting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;
import com.worldventures.dreamtrips.modules.settings.view.cell.delegate.SettingsSelectCellDelegate;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_settings_select)
public class SettingsSelectCell extends AbstractDelegateCell<SelectSetting, SettingsSelectCellDelegate> {

   @InjectView(R.id.settings_title) TextView settingsTitle;
   @InjectView(R.id.settings_value) TextView settingsValue;

   @Inject FragmentManager fragmentManager;

   public SettingsSelectCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      settingsTitle.setText(SettingsManager.getLocalizedTitleResource(getModelObject().getName()));
      settingsValue.setText(SettingsManager.getLocalizedOptionResource(getModelObject().getValue()));
   }

   @OnClick({R.id.settings_item_holder})
   public void onItemClick(View view) {
      showSingleChoiceDialog(view.getContext());
   }

   private void showSingleChoiceDialog(Context context) {
      SelectSetting model = getModelObject();
      SelectDialog dialog = SelectDialog.newInstance(SettingsManager.getSelectDialogModel(context.getResources(), model.getName(), model
            .getOptions(), model.getValue()));
      dialog.setButtonListener(checkedPosition -> {
         model.setValue(getModelObject().getOptions().get(checkedPosition));
         cellDelegate.onValueSelected();
         syncUIStateWithModel();
      });
      dialog.show(fragmentManager);
   }
}
