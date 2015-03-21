package com.worldventures.dreamtrips.modules.common.view.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

import java.util.ArrayList;
import java.util.List;

public class FilterableArrayListAdapter<BaseItemClass> extends LoaderRecycleAdapter<BaseItemClass> {

    protected List<BaseItemClass> cashedItems = new ArrayList<>();

    public FilterableArrayListAdapter(Context context, Injector injector) {
        super(context, injector);
    }

    public void flushFilter() {
        if (!cashedItems.isEmpty()) {
            items.clear();
            items.addAll(cashedItems);
            cashedItems.clear();
            notifyDataSetChanged();
        }
    }

    public void setFilter(String query) {
        if (TextUtils.isEmpty(query)) {
            flushFilter();
        } else {
            if (cashedItems.isEmpty())
                cashedItems.addAll(items);

            items.clear();
            query = query.toLowerCase();

            for (BaseItemClass item : cashedItems) {
                if (item instanceof Filterable) {
                    Filterable filterable = (Filterable) item;
                    if (filterable.containsQuery(query))
                        items.add(item);
                }
            }

            notifyDataSetChanged();
        }
    }
}