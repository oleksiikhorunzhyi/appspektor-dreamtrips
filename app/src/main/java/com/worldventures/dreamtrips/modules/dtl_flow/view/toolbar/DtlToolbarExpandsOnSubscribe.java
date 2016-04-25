package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class DtlToolbarExpandsOnSubscribe implements Observable.OnSubscribe<Void> {

    private final DtlToolbar dtlToolbar;

    public DtlToolbarExpandsOnSubscribe(DtlToolbar dtlToolbar) {
        this.dtlToolbar = dtlToolbar;
    }

    @Override
    public void call(Subscriber<? super Void> subscriber) {
        DtlToolbar.ExpandListener expandListener = new DtlToolbar.ExpandListener() {
            @Override
            public void onExpanded() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                }
            }
        };

        dtlToolbar.addExpandListener(expandListener);

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                dtlToolbar.removeExpandListener(expandListener);
            }
        });
    }
}
