package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityShowEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;

@Layout(R.layout.adapter_item_entity_details)
public class FeedEntityDetailsCell extends BaseFeedCell<FeedItem> {

    public FeedEntityDetailsCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        getEventBus().post(new FeedEntityShowEvent(getModelObject()));
    }

}
