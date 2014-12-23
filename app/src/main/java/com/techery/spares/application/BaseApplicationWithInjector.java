package com.techery.spares.application;

import android.app.Application;

import com.techery.spares.module.Injector;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.ObjectGraph;

public abstract class BaseApplicationWithInjector extends Application implements Injector {
    private ObjectGraph objectGraph;

    @Inject
    Set<AppInitializer> appInitializers;

    @Override
    public void onCreate() {
        super.onCreate();

        this.objectGraph = ObjectGraph.create(getModules().toArray());

        inject(this);

        runInitializers();
    }

    private void runInitializers() {
        for(AppInitializer initializer : appInitializers) {
            initializer.initialize();
        }
    }

    protected abstract List<Object> getModules();

    @Override
    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    @Override
    public void inject(Object target) {
        getObjectGraph().inject(target);
    }
}
