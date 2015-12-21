package com.worldventures.dreamtrips.modules.dtl.helper;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import com.worldventures.dreamtrips.R;

import java.util.concurrent.TimeUnit;

import rx.Subscription;

public class SearchViewHelper {

    private static final int DEBOUNCE_INTERVAL_LENGTH = 900; // milliseconds

    private QueryChangedListener onQueryChangedListener;
    private SearchView searchView;
    private Subscription searchViewSubscription;

    public SearchViewHelper() {
    }

    public void init(MenuItem searchItem, String defValue, QueryChangedListener listener) {
        this.onQueryChangedListener = listener;
        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setQueryHint(searchView.getResources().getString(R.string.search));
            searchView.setOnCloseListener(this::onSearchViewClosed);
            searchView.post(() -> searchView.setQuery(defValue, true));

            searchViewSubscription = RxSearchView.queryTextChangeEvents(searchView)
                    .debounce(DEBOUNCE_INTERVAL_LENGTH, TimeUnit.MILLISECONDS)
                    .subscribe(this::onQueryTextChange);
        }
    }

    public void dropHelper() {
        onSearchViewClosed();
    }

    private boolean onSearchViewClosed() {
        searchViewSubscription.unsubscribe();
        if (onQueryChangedListener != null) {
            onQueryChangedListener.onQueryChanged(null);
        }
        return false;
    }

    public void onQueryTextChange(SearchViewQueryTextEvent event) {
        if (onQueryChangedListener != null) {
            onQueryChangedListener.onQueryChanged(event.queryText().toString());
        }
    }

    public interface QueryChangedListener {
        void onQueryChanged(String query);
    }
}
