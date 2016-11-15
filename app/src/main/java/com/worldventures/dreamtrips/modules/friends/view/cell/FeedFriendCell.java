package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.view.cell.delegate.FriendCellDelegate;

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
