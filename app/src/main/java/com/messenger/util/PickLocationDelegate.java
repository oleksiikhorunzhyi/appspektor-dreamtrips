package com.messenger.util;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;

import com.worldventures.dreamtrips.modules.picklocation.LocationPicker;

import java.lang.ref.WeakReference;
import rx.Notification;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class PickLocationDelegate {

    private WeakReference<Activity> activity;

    private Handler handler = new Handler();

    private PublishSubject<Notification<Location>> pickedLocationsStream = PublishSubject.create();

    public PickLocationDelegate(Activity activity) {
        Timber.d("Location pick location delegate constructor with activity %s", activity);
        this.activity = new WeakReference<>(activity);
    }

    public void pickLocation() {
        if (activity.get() == null) {
            Timber.e("Activity this class was initialized with was deallocated");
            return;
        }
        LocationPicker.start(activity.get());
    }

    public Observable<Notification<Location>> getPickedLocationsStream() {
        return pickedLocationsStream;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return LocationPicker.onActivityResult(requestCode, resultCode, data, this::onLocationPickedResult);
    }

    private void onLocationPickedResult(Location location, Throwable error) {
        if (error != null) {
            sendNotification(Notification.createOnError(error));
        } else if (location != null) {
            sendNotification(Notification.createOnNext(location));
        }
    }

    private void sendNotification(Notification<Location> notification) {
        // TODO Improve this. Workaround for onAttachedToWindow() called after
        // onActivityResult() after user rotated screen in crop activity
        handler.post(() -> pickedLocationsStream.onNext(notification));
    }
}
