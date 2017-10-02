package com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview;

import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;

public interface TagCreationActionsListener extends TagActionListener {

   void requestFriendList(String query, int page);

   void onTagCreated(CreationTagView newTagView, PhotoTag suggestion, PhotoTag tag);

}