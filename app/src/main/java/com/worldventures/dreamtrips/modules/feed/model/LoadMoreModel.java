package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.feed.view.adapter.NotificationHeaderAdapter;

public class LoadMoreModel implements NotificationHeaderAdapter.HeaderItem {

    @Override
    public String getHeaderTitle() {
        return NON_SHOWING_HEADER_VALUE;
    }
}
