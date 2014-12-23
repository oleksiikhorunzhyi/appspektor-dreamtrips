package com.techery.spares.adapter;

public interface Cell<T> {
    public void setObject(T object);

    public interface OnReadyListener {
        void onViewReady();
    }
}
