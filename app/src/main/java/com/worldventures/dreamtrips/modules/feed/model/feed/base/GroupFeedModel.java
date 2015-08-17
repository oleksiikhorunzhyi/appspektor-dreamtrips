package com.worldventures.dreamtrips.modules.feed.model.feed.base;

import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;

import java.util.List;

public class GroupFeedModel<T> extends ParentFeedModel {

    List<T> items;

    public List<T> getItems() {
        return items;
    }
}
