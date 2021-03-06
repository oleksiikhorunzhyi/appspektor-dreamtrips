package com.worldventures.dreamtrips.social.ui.settings.view.fragment;

import com.worldventures.core.modules.settings.model.SelectSetting;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.settings.view.cell.delegate.SettingsSelectCellDelegate;

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
      //do nothing
   }
}
