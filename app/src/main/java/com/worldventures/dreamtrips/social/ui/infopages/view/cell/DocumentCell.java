package com.worldventures.dreamtrips.social.ui.infopages.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.modules.infopages.model.Document;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_document)
public class DocumentCell extends BaseAbstractDelegateCell<Document, CellDelegate<Document>> {

   @InjectView(R.id.documentName) TextView documentName;

   public DocumentCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      documentName.setText(getModelObject().getName());

      itemView.setOnClickListener(view -> cellDelegate.onCellClicked(getModelObject()));
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
