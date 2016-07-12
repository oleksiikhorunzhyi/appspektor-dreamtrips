package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class DtlToolbarCollapsesOnSubscribe implements Observable.OnSubscribe<Void> {

    private final ExpandableDtlToolbar dtlToolbar;

    public DtlToolbarCollapsesOnSubscribe(ExpandableDtlToolbar dtlToolbar) {
        this.dtlToolbar = dtlToolbar;
    }

    @Override
    public void call(Subscriber<? super Void> subscriber) {
        ExpandableDtlToolbar.CollapseListener collapseListener = new ExpandableDtlToolbar.CollapseListener() {
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
