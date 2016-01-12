package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

public interface TagCreationActionsListener extends TagActionListener {

    void requestFriendList(String query);

    void onTagCreated(CreationTagView newTagView, PhotoTag tag);
}