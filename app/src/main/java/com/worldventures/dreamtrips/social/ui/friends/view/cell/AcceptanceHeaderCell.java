package com.worldventures.dreamtrips.social.ui.friends.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.service.users.base.model.AcceptanceHeaderModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_all_friends_accepted)
public class AcceptanceHeaderCell extends BaseAbstractDelegateCell<AcceptanceHeaderModel, CellDelegate<AcceptanceHeaderModel>> {

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

   @Override
   public boolean shouldInject() {
      return false;
   }
}
