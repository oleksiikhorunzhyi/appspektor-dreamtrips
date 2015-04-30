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
    protected String query;

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

    ///////////////////////////////////////////////////////////////////////////
    // Filter manipulation
    ///////////////////////////////////////////////////////////////////////////

    public void setFilter(String query) {
        this.query = query;
        if (TextUtils.isEmpty(query)) {
            flushFilter();
        } else {
            if (cachedItems.isEmpty()) {
                cachedItems.addAll(items);
            }

            filterHandler.post(() -> {
                String queryLowerCased = this.query.toLowerCase();
                List<BaseItemClass> filtered = Queryable.from(cachedItems).filter(item -> item.containsQuery(queryLowerCased)).toList();
                mainHandler.post(() -> {
                    items.clear();
                    items.addAll(filtered);
                    notifyDataSetChanged();
                });
            });
        }
    }

    public void flushFilter() {
        query = null;
        if (!cachedItems.isEmpty()) {
            items.clear();
            items.addAll(cachedItems);
            cachedItems.clear();
            notifyDataSetChanged();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Items modification proxy
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void addItem(int location, BaseItemClass item) {
        if (query == null) super.addItem(location, item);
        else {
            cachedItems.add(location, item);
            setFilter(query);
        }
    }

    @Override
    public void addItems(ArrayList<BaseItemClass> items) {
        if (query == null) super.addItems(items);
        else {
            cachedItems.addAll(items);
            setFilter(query);
        }
    }

    @Override
    public void addItems(List<BaseItemClass> items) {
        if (query == null) super.addItems(items);
        else {
            cachedItems.addAll(items);
            setFilter(query);
        }
    }

    @Override
    public void replaceItem(int location, BaseItemClass item) {
        if (query == null) super.replaceItem(location, item);
        else {
            cachedItems.set(location, item);
            setFilter(query);
        }
    }

    @Override
    public void remove(int location) {
        if (query == null) super.remove(location);
        else {
            cachedItems.remove(location);
            setFilter(query);
        }
    }

    @Override
    public void setItems(List<BaseItemClass> items) {
        this.items = items;
        if (query == null) notifyDataSetChanged();
        else {
            cachedItems.clear();
            cachedItems.addAll(items);
            setFilter(query);
        }
    }

    @Override
    public void clear() {
        super.clear();
        cachedItems.clear();
    }

    @Override
    public void onFinishLoading(List<BaseItemClass> result) {
        super.onFinishLoading(result);
        if (query != null) {
            cachedItems.clear();
            cachedItems.addAll(items);
            setFilter(query);
        }

    }
}
