package com.worldventures.dreamtrips.modules.feed.model.feed.base;

import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;

import java.util.List;

public class ParentFeedModel {

    private String type;
    List<BaseFeedModel> items;

    public List<BaseFeedModel> getItems() {
        return items;
    }

    public String getType() {
        return type;
    }

    public void setItems(List<BaseFeedModel> items) {
        this.items = items;
    }
}
