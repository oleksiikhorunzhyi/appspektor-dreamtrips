package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.model.AcceptanceHeaderModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_all_friends_accepted)
public class AcceptanceHeaderCell extends AbstractDelegateCell<AcceptanceHeaderModel, CellDelegate<AcceptanceHeaderModel>> {

   @InjectView(R.id.tv_accepted) TextView acceptedTextView;

   public AcceptanceHeaderCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      int acceptedCount = getModelObject().getAcceptedCount();
      String header = acceptedTextView.getContext().getString(R.string.n_friends_accepted, acceptedCount);
      acceptedTextView.setText(header);
   }

}
