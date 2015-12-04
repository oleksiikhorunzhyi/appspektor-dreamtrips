package com.messenger.loader;

public abstract class SimpleLoader<T> {
    public interface LoadListener<T> {
        void onLoadSuccess(T data);
        void onError(Throwable error);
    }

    public abstract void loadData(LoadListener<T> listener);
    public abstract T provideData();
}
