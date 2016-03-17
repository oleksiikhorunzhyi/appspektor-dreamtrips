package com.worldventures.dreamtrips.core.rx.viewbinding;

import android.support.v7.widget.SearchView;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

/**
 * Same as {@link com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextChangesOnSubscribe JakeWarton's} but
 * does NOT emit a thing during subscription.
 */
public final class DtSearchViewQueryTextChangesOnSubscribe implements Observable.OnSubscribe<CharSequence> {

    final SearchView view;

    public DtSearchViewQueryTextChangesOnSubscribe(SearchView view) {
        this.view = view;
    }

    @Override public void call(final Subscriber<? super CharSequence> subscriber) {
        checkUiThread();

        SearchView.OnQueryTextListener watcher = new SearchView.OnQueryTextListener() {

            @Override public boolean onQueryTextChange(String s) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(s);
                    return true;
                }
                return false;
            }

            @Override public boolean onQueryTextSubmit(String query) {
                return true;
            }
        };

        view.setOnQueryTextListener(watcher);

        subscriber.add(new MainThreadSubscription() {
            @Override protected void onUnsubscribe() {
                view.setOnQueryTextListener(null);
            }
        });
    }
}
