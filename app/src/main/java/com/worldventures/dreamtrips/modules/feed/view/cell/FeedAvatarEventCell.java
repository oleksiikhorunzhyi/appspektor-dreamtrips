package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.FeedAvatarEventModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_avatar_upload_event)
public class FeedAvatarEventCell extends AbstractCell<FeedAvatarEventModel> {

    @InjectView(R.id.text)
    TextView textView;
    @InjectView(R.id.avatar)
    SimpleDraweeView avater;

    public FeedAvatarEventCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {

    }

    @Override
    public void prepareForReuse() {

    }
}
