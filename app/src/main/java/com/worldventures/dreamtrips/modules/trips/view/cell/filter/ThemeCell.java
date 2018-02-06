package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.trips.model.filter.ActivityModel;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_filter_checkbox)
public class ThemeCell extends BaseAbstractDelegateCell<ActivityModel, ThemeCell.Delegate> {

   @InjectView(R.id.cell) LinearLayout cell;
   @InjectView(R.id.textViewAttributeCaption) TextView textViewName;
   @InjectView(R.id.checkBox) CheckBox checkBox;

   public ThemeCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      textViewName.setText(getModelObject().getName());
      textViewName.setTextColor(getModelObject().isChecked() ? getResources()
            .getColor(R.color.black) : getResources().getColor(R.color.grey));
      checkBox.setChecked(getModelObject().isChecked());
   }

   @OnClick(R.id.checkBox)
   void checkBoxClick() {
      getModelObject().setChecked(checkBox.isChecked());
      cellDelegate.onThemeSetChangedEvent();
   }

   @OnClick(R.id.textViewAttributeCaption)
   void textViewRegionClick() {
      checkBox.setChecked(!checkBox.isChecked());
      getModelObject().setChecked(checkBox.isChecked());
      cellDelegate.onThemeSetChangedEvent();
   }

   @Override
   public void prepareForReuse() {
      textViewName.setText("");
   }

   @Override
   public boolean shouldInject() {
      return false;
   }

   public interface Delegate extends CellDelegate<ActivityModel> {

      void onThemeSetChangedEvent();
   }
}
