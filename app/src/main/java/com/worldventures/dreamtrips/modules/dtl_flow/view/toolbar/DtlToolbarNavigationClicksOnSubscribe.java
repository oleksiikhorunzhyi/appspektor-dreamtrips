package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class DtlToolbarNavigationClicksOnSubscribe implements Observable.OnSubscribe<Void> {

    private final DtlToolbar dtlToolbar;

    public DtlToolbarNavigationClicksOnSubscribe(DtlToolbar dtlToolbar) {
        this.dtlToolbar = dtlToolbar;
    }

    @Override
    public void call(Subscriber<? super Void> subscriber) {
        DtlToolbar.NavigationClickListener navigationClickListener = new DtlToolbar.NavigationClickListener() {
            @Override
            public void onNavigationClicked() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                }
            }
        };

        dtlToolbar.addNavigationClickListener(navigationClickListener);

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                dtlToolbar.removeNavigationClickListener(navigationClickListener);
            }
        });
    }
}
