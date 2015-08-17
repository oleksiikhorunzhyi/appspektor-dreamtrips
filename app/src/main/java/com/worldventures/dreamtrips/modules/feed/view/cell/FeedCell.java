package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;

public class FeedCell extends AbstractCell<ParentFeedModel> {

    public FeedCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        BaseFeedModel baseFeedModel = getModelObject().getItems().get(0);
    }

    @Override
    public void prepareForReuse() {

    }
}
