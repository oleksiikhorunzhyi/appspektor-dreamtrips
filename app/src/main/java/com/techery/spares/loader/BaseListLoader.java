package com.techery.spares.loader;

/**
 * Created by zen on 11/13/13.
 */

import android.content.Context;

import java.util.List;

public class BaseListLoader<T> extends BaseSimpleTaskLoader<List<T>> {
    public BaseListLoader(Context context, LoadingTask<List<T>> loadingTask) {
        super(context, loadingTask);
    }
}