package com.worldventures.dreamtrips.modules.dtl.helper;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.transfromer.DelayedComposer;

import rx.Subscription;
import timber.log.Timber;

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
            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener(){
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    searchViewSubscription = RxSearchView.queryTextChangeEvents(searchView)
                            .compose(new DelayedComposer<>(DEBOUNCE_INTERVAL_LENGTH))
                            .subscribe(SearchViewHelper.this::onQueryTextChange, e ->
                                    Timber.e("Fail while search", e));
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    onSearchViewClosed();
                    return true;
                }
            });

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setQueryHint(searchView.getResources().getString(R.string.search));
            searchView.post(() -> searchView.setQuery(defValue, true));
        }
    }

    public void dropHelper() {
        unsubcribe();
        onQueryChangedListener = null;
    }

    private void onSearchViewClosed() {
        unsubcribe();
        if (onQueryChangedListener != null) {
            onQueryChangedListener.onQueryChanged(null);
        }
    }

    private void unsubcribe() {
        if (searchViewSubscription != null) searchViewSubscription.unsubscribe();
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
