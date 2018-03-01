package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.trips.model.filter.ThemeHeaderModel;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_activity_header)
public class HeaderThemeCell extends BaseAbstractDelegateCell<ThemeHeaderModel, HeaderThemeCell.Delegate> {

   @InjectView(R.id.checkBoxSelectAllTheme) CheckBox checkBoxSelectAll;

   public HeaderThemeCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      checkBoxSelectAll.setChecked(getModelObject().isChecked());
   }

   @OnClick(R.id.checkBoxSelectAllTheme)
   void checkBoxClicked() {
      cellDelegate.onCheckBoxAllThemePressedEvent(checkBoxSelectAll.isChecked());
   }

   @OnClick(R.id.listHeader)
   void toggleVisibility() {
      cellDelegate.toggleVisibility();
   }

   @Override
   public boolean shouldInject() {
      return false;
   }

   public interface Delegate extends CellDelegate<ThemeHeaderModel> {
      void toggleVisibility();

      void onCheckBoxAllThemePressedEvent(boolean isChecked);
   }
}

