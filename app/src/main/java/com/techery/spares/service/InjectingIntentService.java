package com.techery.spares.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;

import com.techery.spares.module.Injector;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public abstract class InjectingIntentService extends IntentService implements Injector {
    private ObjectGraph objectGraph;

    public InjectingIntentService(String name) {
        super(name);
    }

    @Override
    public ObjectGraph getObjectGraph() {
        return this.objectGraph;
    }

    @Override
    public void inject(Object target) {
        getObjectGraph().inject(target);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ((Injector) getApplication()).getObjectGraph().plus(getModules().toArray());

        getObjectGraph().inject(this);
    }

    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        return result;
    }
}
