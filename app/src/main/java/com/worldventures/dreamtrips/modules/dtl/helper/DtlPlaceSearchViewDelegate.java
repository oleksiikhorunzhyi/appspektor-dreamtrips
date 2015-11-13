package com.worldventures.dreamtrips.modules.dtl.helper;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import com.worldventures.dreamtrips.R;

public class DtlPlaceSearchViewDelegate implements SearchView.OnQueryTextListener {

    private Context c;
    private QueryChangedListener onQueryChangedListener;
    private SearchView searchView;

    public DtlPlaceSearchViewDelegate(Context c) {
        this.c = c;
    }

    public void init(MenuItem searchItem, String defValue, QueryChangedListener listener) {
        this.onQueryChangedListener = listener;
        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setQueryHint(c.getString(R.string.search));
            searchView.setOnCloseListener(() -> {
                if (onQueryChangedListener != null) {
                    onQueryChangedListener.onQueryChanged(null);
                }
                return false;
            });
            searchView.setOnQueryTextListener(this);
            searchView.post(() -> searchView.setQuery(defValue, true));
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (onQueryChangedListener != null) {
            onQueryChangedListener.onQueryChanged(query);
        }
        if (searchView != null) {
            searchView.clearFocus();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (onQueryChangedListener != null) {
            onQueryChangedListener.onQueryChanged(newText);
        }
        return false;
    }

    public interface QueryChangedListener {
        void onQueryChanged(String query);
    }
}
