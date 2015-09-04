package com.worldventures.dreamtrips.modules.feed.view.adapter;

import android.content.Context;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;

import java.util.List;

import javax.inject.Provider;

public class DiffArrayListAdapter<T> extends BaseArrayListAdapter<T> {

    public DiffArrayListAdapter(Context context, Provider<Injector> injector) {
        super(context, injector);
    }

    public void itemsUpdated(List<T> updatedItems) {
        clear();
        getItems().addAll(updatedItems);
        notifyDataSetChanged();
    }
}
