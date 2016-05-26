package com.worldventures.dreamtrips.modules.map.reactive;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static rx.android.MainThreadSubscription.verifyMainThread;

public class MarkerClickObservable implements Observable.OnSubscribe<Marker> {
    private final GoogleMap map;

    public static Observable<Marker> create(GoogleMap map){
        return Observable.create(new MarkerClickObservable(map));
    }

    public MarkerClickObservable(GoogleMap map) {
        this.map = map;
    }

    @Override
    public void call(final Subscriber<? super Marker> subscriber) {
        verifyMainThread();

        GoogleMap.OnMarkerClickListener listener = marker -> {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(marker);
            }
            return true;
        };
        map.setOnMarkerClickListener(listener);

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                map.setOnMarkerClickListener(null);
            }
        });
    }
}
