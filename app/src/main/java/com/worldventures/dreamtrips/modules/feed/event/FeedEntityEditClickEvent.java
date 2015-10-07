package com.worldventures.dreamtrips.modules.feed.event;

import android.view.View;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class FeedEntityEditClickEvent {
    private FeedItem feedItem;
    View anchor;

    public FeedEntityEditClickEvent(FeedItem feedItem, View anchor) {
        this.feedItem = feedItem;
        this.anchor = anchor;
    }


    public FeedItem getFeedItem() {
        return feedItem;
    }

    public View getAnchor() {
        return anchor;
    }
}
