package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.FeedAvatarEventModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_cover_upload_event)
public class FeedCoverEventCell extends AbstractCell<FeedAvatarEventModel> {

    @InjectView(R.id.cover)
    SimpleDraweeView cover;

    public FeedCoverEventCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {

    }

    @Override
    public void prepareForReuse() {

    }
}
