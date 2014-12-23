package com.techery.spares.ui.utils;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class ItemClickHandler<T> implements AdapterView.OnItemClickListener {

    public ItemClickHandler(ArrayAdapter<T> adapter, Listener<T> listener) {
        this.adapter = adapter;
        this.listener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.onItemSelected(adapter.getItem(position));
    }

    public interface Listener<T> {
        public void onItemSelected(T item);
    }

    private final ArrayAdapter<T> adapter;
    private final Listener<T> listener;

}
