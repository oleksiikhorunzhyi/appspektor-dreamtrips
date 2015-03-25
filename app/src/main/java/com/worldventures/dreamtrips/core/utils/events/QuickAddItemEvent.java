package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;

/**
 *  1 on 19.03.15.
 */
public class QuickAddItemEvent {

    private BucketTabsFragment.Type type;

    public QuickAddItemEvent(BucketTabsFragment.Type type) {
        this.type = type;
    }

    public BucketTabsFragment.Type getType() {
        return type;
    }

    public void setType(BucketTabsFragment.Type type) {
        this.type = type;
    }
}
