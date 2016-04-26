package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

public interface TagCreationActionsListener extends TagActionListener {

    void requestFriendList(String query, int page);

    void onTagCreated(CreationTagView newTagView, PhotoTag suggestion, PhotoTag tag);

}