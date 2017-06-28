package com.worldventures.dreamtrips.wallet.ui.settings.common.cell;

import android.view.View;
import android.widget.CheckBox;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;
import com.worldventures.dreamtrips.core.selectable.SelectableDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.list_item_disable_default_card)
public class SettingsRadioCell extends AbstractDelegateCell<SettingsRadioModel, SettingsRadioCell.Delegate> implements SelectableCell {

   @InjectView(R.id.checkbox) CheckBox checkBox;
   @InjectView(R.id.divider) View divider;

   private SelectableDelegate selectableDelegate;

   public SettingsRadioCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      checkBox.setText(getModelObject().getText());
      checkBox.setChecked(selectableDelegate.isSelected(getAdapterPosition()));
      divider.setVisibility(cellDelegate.isLast(getAdapterPosition()) ? View.GONE : View.VISIBLE);
   }

   @OnClick(R.id.checkbox)
   void checkBoxClicked() {
      selectableDelegate.toggleSelection(getAdapterPosition());
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   public void setSelectableDelegate(SelectableDelegate selectableDelegate) {
      this.selectableDelegate = selectableDelegate;
   }

   public interface Delegate extends CellDelegate<SettingsRadioModel> {

      boolean isLast(int position);
   }
}
