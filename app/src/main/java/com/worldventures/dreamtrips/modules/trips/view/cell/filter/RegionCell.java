package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_filter_checkbox)
public class RegionCell extends AbstractDelegateCell<RegionModel, RegionCell.Delegate> {

   @InjectView(R.id.textViewAttributeCaption) TextView textViewName;
   @InjectView(R.id.checkBox) CheckBox checkBox;
   @InjectView(R.id.cell) LinearLayout cell;

   @Inject Context context;

   public RegionCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      textViewName.setText(getModelObject().getName());
      textViewName.setTextColor(getModelObject().isChecked() ? context.getResources()
            .getColor(R.color.black) : context.getResources().getColor(R.color.grey));
      checkBox.setChecked(getModelObject().isChecked());
   }

   @OnClick(R.id.checkBox)
   void checkBoxClick() {
      getModelObject().setChecked(checkBox.isChecked());
      cellDelegate.onRegionSetChangedEvent();
   }

   @OnClick(R.id.textViewAttributeCaption)
   void textViewRegionClick() {
      checkBox.setChecked(!checkBox.isChecked());
      getModelObject().setChecked(checkBox.isChecked());
      cellDelegate.onRegionSetChangedEvent();
   }

   @Override
   public void prepareForReuse() {
      textViewName.setText("");
   }

   public interface Delegate extends CellDelegate<RegionModel> {
      void onRegionSetChangedEvent();
   }
}
