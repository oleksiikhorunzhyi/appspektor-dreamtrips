package com.worldventures.dreamtrips.modules.infopages.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.infopages.model.Document;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_document)
public class DocumentCell extends AbstractDelegateCell<Document, CellDelegate<Document>> {

   @InjectView(R.id.documentName) TextView documentName;

   public DocumentCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      documentName.setText(getModelObject().getName());

      itemView.setOnClickListener(view -> cellDelegate.onCellClicked(getModelObject()));
   }
}
