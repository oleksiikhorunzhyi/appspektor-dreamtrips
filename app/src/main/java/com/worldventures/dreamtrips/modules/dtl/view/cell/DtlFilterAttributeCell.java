package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;
import com.worldventures.dreamtrips.core.selectable.SelectableDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_filter_checkbox)
public class DtlFilterAttributeCell extends AbstractDelegateCell<DtlMerchantAttribute, CellDelegate<DtlMerchantAttribute>> implements SelectableCell {

   @InjectView(R.id.textViewAttributeCaption) protected TextView textViewName;
   @InjectView(R.id.checkBox) protected CheckBox checkBox;
   //
   private SelectableDelegate selectableDelegate;

   public DtlFilterAttributeCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      textViewName.setText(getModelObject().getName());
      checkBox.setChecked(selectableDelegate.isSelected(getAdapterPosition()));
   }

   @OnClick(R.id.checkBox)
   void checkBoxClicked() {
      selectableDelegate.toggleSelection(getAdapterPosition());
      cellDelegate.onCellClicked(getModelObject());
   }

   @OnClick(R.id.textViewAttributeCaption)
   void textViewRegionClick() {
      checkBox.setChecked(!checkBox.isChecked());
      checkBoxClicked();
   }

   @Override
   public void setSelectableDelegate(SelectableDelegate selectableDelegate) {
      this.selectableDelegate = selectableDelegate;
   }

   @Override
   public void prepareForReuse() {
      textViewName.setText("");
   }
}
