package com.techery.spares.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

public interface ContentLoader<T> {
    public interface LoaderCreator {
        public Loader createLoader(final Context context, Bundle bundle);
    }

    public interface ContentLoadingObserving<T> {
        public void onStartLoading();
        public void onFinishLoading(T result);
        public void onError(Throwable throwable);
    }

    public void load();
    public void reload();

    public T getResult();

    public ContentLoaderObserver<T> getContentLoaderObserver();

}
