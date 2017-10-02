package com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate;

import com.worldventures.core.model.User;

public interface RequestCellDelegate extends UserActionDelegate {

   void acceptRequest(User user);

   void rejectRequest(User user);

   void hideRequest(User user);

   void cancelRequest(User user);
}
