package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import android.content.Context;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;

import java.util.List;

import javax.inject.Provider;

public class IgnoreFirstItemAdapter extends BaseArrayListAdapter {

    public IgnoreFirstItemAdapter(Context context, Provider<Injector> injector) {
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

    @Override
    public List getItems() {
        return super.getItems().subList(1, getCount());
    }
}
