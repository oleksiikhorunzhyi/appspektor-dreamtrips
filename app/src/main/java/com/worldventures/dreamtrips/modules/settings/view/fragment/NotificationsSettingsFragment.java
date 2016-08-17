package com.worldventures.dreamtrips.modules.settings.view.fragment;

import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.model.FlagSetting;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

@Layout(R.layout.fragment_settings_notifications)
@MenuResource(R.menu.menu_settings)
public class NotificationsSettingsFragment extends SettingsFragment implements CellDelegate<Setting> {

   private MenuItem buttonDone;

   @Override
   protected void registerCells() {
      super.registerCells();
      adapter.registerDelegate(FlagSetting.class, this);
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      buttonDone = menu.findItem(R.id.done);
      buttonDone.setEnabled(getPresenter().isSettingsChanged());
   }

   @Override
   protected void setupToolbar() {
      super.setupToolbar();
      toolbar.setNavigationOnClickListener(v -> {
         if (getPresenter().isSettingsChanged()) {
            showChangesDialog();
         } else {
            close();
         }
      });
   }

   private void showChangesDialog() {
      new MaterialDialog.Builder(getContext()).title(R.string.save_changes_before_proceed)
            .positiveText(R.string.save)
            .negativeText(R.string.discard)
            .onPositive((materialDialog, dialogAction) -> getPresenter().applyChanges())
            .onNegative((materialDialog1, dialogAction1) -> close())
            .show();
   }

   @Override
   public void onAppliedChanges() {
      close();
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.done:
            getPresenter().applyChanges();
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   private void validateDoneButton() {
      buttonDone.setEnabled(getPresenter().isSettingsChanged());
   }

   @Override
   public void onCellClicked(Setting model) {
      validateDoneButton();
   }
}
