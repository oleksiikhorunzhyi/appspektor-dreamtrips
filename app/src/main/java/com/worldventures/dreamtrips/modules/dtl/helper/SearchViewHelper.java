package com.worldventures.dreamtrips.modules.dtl.helper;

import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.DelayedComposer;

import rx.Subscription;
import timber.log.Timber;

public class SearchViewHelper {

   // TODO :: 4/24/16 delete after merge of new toolbar
   private static final int THROTTLE_SEARCH_DURATION = 900;

   private QueryChangedListener onQueryChangedListener;
   private SearchClosedListener onSearchClosedListener;
   private SearchView searchView;
   private Subscription searchViewSubscription;

   public SearchViewHelper() {
   }

   public void init(MenuItem searchItem, String defValue, QueryChangedListener listener) {
      init(searchItem, defValue, listener, null);
   }

   public void init(MenuItem searchItem, String defValue, QueryChangedListener listener, @Nullable SearchClosedListener searchClosedListener) {
      this.onSearchClosedListener = searchClosedListener;
      this.onQueryChangedListener = listener;
      if (searchItem != null) {
         MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
               searchViewSubscription = RxSearchView.queryTextChangeEvents(searchView)
                     .compose(new DelayedComposer<>(THROTTLE_SEARCH_DURATION))
                     .distinctUntilChanged()
                     .subscribe(SearchViewHelper.this::onQueryTextChange, e -> Timber.e("Fail while search", e));
               return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
               onSearchViewClosed();
               return true;
            }
         });
         //
         searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
         searchView.setQueryHint(searchView.getResources().getString(R.string.search));
         searchView.post(() -> searchView.setQuery(defValue, true));
      }
   }

   public void dropHelper() {
      unsubcribe();
      onQueryChangedListener = null;
      onSearchClosedListener = null;
   }

   private void onSearchViewClosed() {
      unsubcribe();
      if (onSearchClosedListener != null) {
         onSearchClosedListener.onSearchClosed();
      }
      if (onQueryChangedListener != null) {
         onQueryChangedListener.onQueryChanged("");
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

   public interface SearchClosedListener {
      void onSearchClosed();
   }

   public interface QueryChangedListener {
      void onQueryChanged(String query);
   }
}
