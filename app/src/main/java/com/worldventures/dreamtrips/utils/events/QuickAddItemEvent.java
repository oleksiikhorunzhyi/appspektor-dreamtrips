package com.worldventures.dreamtrips.utils.events;

import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

/**
 * Created by 1 on 19.03.15.
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
