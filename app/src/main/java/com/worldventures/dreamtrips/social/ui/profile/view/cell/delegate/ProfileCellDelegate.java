package com.worldventures.dreamtrips.social.ui.profile.view.cell.delegate;

import com.worldventures.core.model.User;
import com.worldventures.core.ui.view.cell.CellDelegate;

public interface ProfileCellDelegate extends CellDelegate<User> {

   void onBucketListClicked();

   void onTripImagesClicked();

   void onFriendsClicked();

   void onCreatePostClicked();

   void onUserPhotoClicked();

   void onUserCoverClicked();

   void onAcceptRequest();

   void onRejectRequest();

   void onAddFriend();
}
