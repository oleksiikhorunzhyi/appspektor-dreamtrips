package com.worldventures.dreamtrips.modules.common.view.adapter;

import android.content.Context;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

import java.util.ArrayList;
import java.util.List;

public class FilterableArrayListAdapter<BaseItemClass extends Filterable> extends LoaderRecycleAdapter<BaseItemClass> {

    protected List<BaseItemClass> cachedItems;

    protected WeakHandler mainHandler;
    protected WeakHandler filterHandler;

    public FilterableArrayListAdapter(Context context, Injector injector) {
        super(context, injector);
        cachedItems = new ArrayList<>();

        mainHandler = new WeakHandler();
        HandlerThread filterThread = new HandlerThread("filter");
        filterThread.start();
        filterHandler = new WeakHandler(filterThread.getLooper());
    }

    public void flushFilter() {
        if (!cachedItems.isEmpty()) {
            items.clear();
            items.addAll(cachedItems);
            cachedItems.clear();
            notifyDataSetChanged();
        }
    }

    public void setFilter(String query) {
        if (TextUtils.isEmpty(query)) {
            flushFilter();
        } else {
            if (cachedItems.isEmpty()) {
                cachedItems.addAll(items);
            }

            filterHandler.post(() -> {
                String queryLowerCased = query.toLowerCase();
                List<BaseItemClass> filtered = Queryable.from(cachedItems).filter(item -> item.containsQuery(queryLowerCased)).toList();
                mainHandler.post(() -> {
                    items.clear();
                    items.addAll(filtered);
                    notifyDataSetChanged();
                });
            });
        }
    }
}
