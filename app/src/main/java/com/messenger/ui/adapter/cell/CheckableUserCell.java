package com.messenger.ui.adapter.cell;

import android.view.View;

import com.messenger.entities.DataUser;
import com.messenger.ui.model.SelectableDataUser;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.list_item_contact)
public class CheckableUserCell extends UserCell<SelectableDataUser, CellDelegate<SelectableDataUser>> {

   public CheckableUserCell(View view) {
      super(view);
      tickImageView.setVisibility(View.VISIBLE);
      view.setOnClickListener(v -> onSelectChanged());
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      tickImageView.setSelected(getModelObject().isSelected());
   }

   @Override
   protected DataUser getDataUser() {
      return getModelObject().getDataUser();
   }

   void onSelectChanged() {
      SelectableDataUser model = getModelObject();
      if (!model.isSelectionEnabled()) {
         return;
      }
      boolean selected = !model.isSelected();
      model.setSelected(selected);
      tickImageView.setSelected(selected);
      cellDelegate.onCellClicked(model);
   }
}
