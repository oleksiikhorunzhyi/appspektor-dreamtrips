package com.worldventures.dreamtrips.modules.tripsimages.view.util;

import com.worldventures.dreamtrips.modules.trips.model.Location;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class PostLocationPickerCallback {

    private final Subject<Location, Location> bus = new SerializedSubject<>(PublishSubject.create());

    public Observable<Location> toObservable() {
        return bus;
    }

    public void onLocationPicked(Location location) {
        bus.onNext(location);
    }
}
