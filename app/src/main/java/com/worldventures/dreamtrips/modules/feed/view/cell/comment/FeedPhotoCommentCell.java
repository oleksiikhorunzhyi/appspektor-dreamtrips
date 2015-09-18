package com.worldventures.dreamtrips.modules.feed.view.cell.comment;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPhotoEventCell;

@Layout(R.layout.adapter_item_feed_photo_comment)
public class FeedPhotoCommentCell extends FeedPhotoEventCell {
    public FeedPhotoCommentCell(View view) {
        super(view);
    }

    @Override
    protected void openComments(BaseEventModel baseFeedModel) {
        //do nothing
    }

}
