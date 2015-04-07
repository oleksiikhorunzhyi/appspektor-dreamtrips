package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import android.content.Context;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;

public class BucketImageAdapter extends BaseArrayListAdapter {
    public BucketImageAdapter(Context context, Injector injector) {
        super(context, injector);
    }

    @Override
    public void clear() {
        Object item = null;
        if (!items.isEmpty()) {
            item = getItem(0);
        }
        super.clear();
        if (item != null) {
            addItem(item);
        }
    }
}
