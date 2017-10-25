package com.worldventures.dreamtrips.social.ui.friends.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.model.User;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate.UserSearchCellDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_user_search)
public class UserSearchCell extends BaseUserCell<UserSearchCellDelegate> {

   @InjectView(R.id.add) TextView add;
   @InjectView(R.id.add_wrapper) View addWrapper;
   @InjectView(R.id.pending_wrapper) View pendingWrapper;

   public UserSearchCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      if (getModelObject().getRelationship() == User.Relationship.OUTGOING_REQUEST) {
         addWrapper.setVisibility(View.GONE);
         pendingWrapper.setVisibility(View.VISIBLE);
      } else {
         pendingWrapper.setVisibility(View.GONE);
         addWrapper.setVisibility(View.VISIBLE);
      }
   }

   @OnClick(R.id.add)
   void onAccept() {
      cellDelegate.addUserRequest(getModelObject());
   }
}
