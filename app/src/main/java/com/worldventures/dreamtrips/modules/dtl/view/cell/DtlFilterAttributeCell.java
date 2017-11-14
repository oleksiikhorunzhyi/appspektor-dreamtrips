package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;
import com.worldventures.dreamtrips.core.selectable.SelectableDelegate;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableAttribute;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_filter_checkbox)
public class DtlFilterAttributeCell
      extends BaseAbstractDelegateCell<ImmutableAttribute, CellDelegate<ImmutableAttribute>> implements SelectableCell {

   @InjectView(R.id.textViewAttributeCaption) protected TextView textViewName;
   @InjectView(R.id.checkBox) protected CheckBox checkBox;

   private SelectableDelegate selectableDelegate;

   public DtlFilterAttributeCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      textViewName.setText(TextUtils.isEmpty(getModelObject().displayName())
            ? getModelObject().name() : getModelObject().displayName());
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
