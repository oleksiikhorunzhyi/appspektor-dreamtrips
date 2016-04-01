package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;

@Layout(R.layout.adapter_item_feed_friend)
public class FeedFriendCell extends BaseUserCell<CellDelegate<User>> {

    public FeedFriendCell(View view) {
        super(view);
    }

    @Override
    protected String createMutualString() {
        return mutualFriendsUtil.createCircleAndMutualString(getModelObject());
    }
}
