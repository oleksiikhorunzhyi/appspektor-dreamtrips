package com.messenger.loader;

import android.os.Handler;
import android.os.Looper;

public abstract class MockLoader<T> extends SimpleLoader<T> {

    private Handler handler = new Handler(Looper.getMainLooper());

    public void loadData(final LoadListener<T> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }
        // simulate loading
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                if (listener != null) {
                    handler.post(new Runnable() {
                        @Override public void run() {
                            listener.onLoadSuccess(provideData());
                        }
                    });
                }
            }
        }, 1000);
    }
}
