package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.view.View;

import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemHeaderHelper;

import butterknife.ButterKnife;

public abstract class FeedHeaderCell<T extends BaseFeedModel> extends AbstractCell<T> {

    private boolean syncUIStateWithModelWasCalled = false;

    FeedItemHeaderHelper feedItemHeaderHelper;

    public FeedHeaderCell(View view) {
        super(view);
        feedItemHeaderHelper = new FeedItemHeaderHelper();
        ButterKnife.inject(feedItemHeaderHelper, view);
    }

    @Override
    protected void syncUIStateWithModel() {
        feedItemHeaderHelper.set(getModelObject());
        syncUIStateWithModelWasCalled = true;
    }


    @Override
    public void fillWithItem(T item) {
        syncUIStateWithModelWasCalled = false;
        super.fillWithItem(item);
        if (!syncUIStateWithModelWasCalled) {
            throw new IllegalStateException("syncUIStateWithModel was not called");
        }

    }
}
