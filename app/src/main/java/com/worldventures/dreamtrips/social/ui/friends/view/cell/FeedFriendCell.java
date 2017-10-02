package com.worldventures.dreamtrips.social.ui.friends.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate.FriendCellDelegate;

@Layout(R.layout.adapter_item_feed_friend)
public class FeedFriendCell extends BaseUserCell<FriendCellDelegate> {

   public FeedFriendCell(View view) {
      super(view);
   }

   @Override
   protected String createMutualString() {
      return mutualFriendsUtil.createCircleAndMutualString(getModelObject());
   }
}
