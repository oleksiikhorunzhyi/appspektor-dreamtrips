package com.worldventures.dreamtrips.modules.map.reactive;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.internal.Preconditions;

import rx.Observable;
import rx.Subscriber;

public class CameraChangeObservable implements Observable.OnSubscribe<CameraPosition> {
    private final GoogleMap map;

    public static Observable<CameraPosition> create(GoogleMap map) {
        return Observable.create(new CameraChangeObservable(map));
    }

    public CameraChangeObservable(GoogleMap map) {
        this.map = map;
    }

    @Override
    public void call(final Subscriber<? super CameraPosition> subscriber) {
        Preconditions.checkUiThread();

        GoogleMap.OnCameraChangeListener listener = cameraPosition -> {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(cameraPosition);
            }
        };
        map.setOnCameraChangeListener(listener);

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                map.setOnCameraChangeListener(null);
            }
        });
    }
}
