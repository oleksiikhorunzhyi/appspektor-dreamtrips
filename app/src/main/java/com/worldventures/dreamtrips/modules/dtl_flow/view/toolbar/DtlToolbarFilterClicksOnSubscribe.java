package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class DtlToolbarFilterClicksOnSubscribe implements Observable.OnSubscribe<Void> {

    private final DtlToolbar dtlToolbar;

    public DtlToolbarFilterClicksOnSubscribe(DtlToolbar dtlToolbar) {
        this.dtlToolbar = dtlToolbar;
    }

    @Override
    public void call(Subscriber<? super Void> subscriber) {
        ExpandableDtlToolbar.FilterButtonListener filterButtonListener =
                new ExpandableDtlToolbar.FilterButtonListener() {
                    @Override
                    public void onFilterButtonClicked() {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
                        }
                    }
                };

        dtlToolbar.addFilterButtonListener(filterButtonListener);

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                dtlToolbar.removeFilterButtonListener(filterButtonListener);
            }
        });
    }
}
