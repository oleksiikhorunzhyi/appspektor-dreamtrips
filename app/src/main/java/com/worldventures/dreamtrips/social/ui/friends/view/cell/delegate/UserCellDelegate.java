package com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate;


import com.worldventures.core.model.User;

public interface UserCellDelegate extends FriendCellDelegate {

   void acceptRequest(User user);

   void addUserRequest(User user);
}
