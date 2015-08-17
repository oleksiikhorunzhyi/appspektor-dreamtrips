package com.worldventures.dreamtrips.modules.feed.model.feed.base;

import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;

public class SingleFeedModel<T> extends ParentFeedModel {

    T item;

    public T getItem() {
        return item;
    }
}
