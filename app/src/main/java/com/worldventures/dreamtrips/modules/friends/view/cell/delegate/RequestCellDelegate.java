package com.worldventures.dreamtrips.modules.friends.view.cell.delegate;

import com.worldventures.dreamtrips.modules.common.model.User;

public interface RequestCellDelegate extends UserActionDelegate {

   void acceptRequest(User user);

   void rejectRequest(User user);

   void hideRequest(User user);

   void cancelRequest(User user);
}
