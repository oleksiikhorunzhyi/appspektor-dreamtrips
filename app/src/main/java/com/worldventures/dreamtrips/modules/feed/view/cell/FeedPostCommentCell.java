package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.adapter_post_comment_event)
public class FeedPostCommentCell extends FeedPostEventCell {

    public FeedPostCommentCell(View view) {
        super(view);
    }

    @Override
    protected void itemClicked() {
    }
}
