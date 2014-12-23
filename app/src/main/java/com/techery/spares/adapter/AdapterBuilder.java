package com.techery.spares.adapter;

import android.content.Context;

import com.techery.spares.module.Injector;

import java.util.List;

import dagger.ObjectGraph;

public class AdapterBuilder {
    private final Injector injector;
    private final Context context;

    public AdapterBuilder(Injector injector, Context context) {
        this.injector = injector;
        this.context = context;
    }

    public <T> BaseArrayAdapter<T> build(List<T> objects, Class<? extends Cell<T>> cellClass) {
        return new BaseArrayAdapter<T>(this.context, injector, objects, cellClass);
    }
}
