package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class DtlToolbarCollapsesOnSubscribe implements Observable.OnSubscribe<Void> {

    private final DtlToolbar dtlToolbar;

    public DtlToolbarCollapsesOnSubscribe(DtlToolbar dtlToolbar) {
        this.dtlToolbar = dtlToolbar;
    }

    @Override
    public void call(Subscriber<? super Void> subscriber) {
        DtlToolbar.CollapseListener collapseListener = new DtlToolbar.CollapseListener() {
            @Override
            public void onCollapsed() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                }
            }
        };

        dtlToolbar.addCollapseListener(collapseListener);

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                dtlToolbar.removeCollapseListener(collapseListener);
            }
        });
    }
}
