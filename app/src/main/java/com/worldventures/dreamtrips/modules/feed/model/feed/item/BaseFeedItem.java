package com.worldventures.dreamtrips.modules.feed.model.feed.item;

import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;

public class BaseFeedItem<T extends IFeedObject> {

    private String action;
    private String type;

    private Links links;

    private T item;

    public String getAction() {
        return action;
    }

    public String getType() {
        return type;
    }

    public Links getLinks() {
        return links;
    }

    public T getItem() {
        return item;
    }
}
