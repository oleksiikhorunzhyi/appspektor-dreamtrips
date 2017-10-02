package com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate;


import com.worldventures.core.model.User;

public interface FriendCellDelegate extends UserActionDelegate {

   void onOpenPrefs(User user);

   void onStartSingleChat(User user);

   void onUnfriend(User user);
}
