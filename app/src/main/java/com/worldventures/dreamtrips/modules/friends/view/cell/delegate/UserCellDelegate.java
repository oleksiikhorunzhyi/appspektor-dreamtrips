package com.worldventures.dreamtrips.modules.friends.view.cell.delegate;

import com.worldventures.dreamtrips.modules.common.model.User;

public interface UserCellDelegate extends FriendCellDelegate {

   void acceptRequest(User user);

   void addUserRequest(User user);
}
