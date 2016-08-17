package com.worldventures.dreamtrips.modules.profile.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.model.User;

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
