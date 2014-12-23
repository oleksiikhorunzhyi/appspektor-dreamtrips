package com.techery.spares.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

/**
 * Created by zen on 10/28/13.
 */
public class BaseSimpleTaskLoader<T> extends BaseAbstractLoader<T> {
    private final LoadingTask<T> loadingTask;


    public static <T> ContentLoader.LoaderCreator buildCreator(final BaseSimpleTaskLoader.LoadingTask<T> loadingTask) {
        return new ContentLoader.LoaderCreator() {
            @Override
            public Loader createLoader(final Context context, Bundle bundle) {
                return new BaseSimpleTaskLoader<T>(context, loadingTask);
            }
        };
    }


    public interface LoadingTask<T> {
        T call(Context context, Bundle params);
    }

    public BaseSimpleTaskLoader(Context context, LoadingTask<T> loadingTask) {
        super(context);
        this.loadingTask = loadingTask;
    }

    protected T perform() {
        return this.loadingTask.call(getContext(), getParams());
    }
}