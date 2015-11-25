package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.adapter_item_feed_friend)
public class FeedFriendCell extends BaseUserCell {

    public FeedFriendCell(View view) {
        super(view);
    }

    @Override
    protected String createMutualString() {
        return mutualStringUtil.createCircleAndMutualString(getModelObject());
    }
}
