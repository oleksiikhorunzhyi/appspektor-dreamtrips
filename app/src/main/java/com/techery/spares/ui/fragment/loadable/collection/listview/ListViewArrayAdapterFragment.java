package com.techery.spares.ui.fragment.loadable.collection.listview;

import android.widget.ListView;

import com.techery.spares.ui.fragment.loadable.collection.ArrayAdapterFragment;

public abstract class ListViewArrayAdapterFragment<T> extends ArrayAdapterFragment<T, ListView> {
    @Override
    protected void setupOnItemClickListener(ListView listView) {
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void linkAdapter(ListView listView) {
        listView.setAdapter(getDataAdapter());
    }
}
