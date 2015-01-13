package com.techery.spares.application;

import android.app.Application;

import com.techery.spares.module.InjectingServiceModule;
import com.techery.spares.module.Injector;
import com.techery.spares.utils.ModuleHelper;

import java.util.ArrayList;
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
            initializer.initialize(this);
        }
    }

    protected List<Object> getModules() {
        List<Object> result = new ArrayList<>();

        Object usedModule = ModuleHelper.getUsedModule(this);

        if (usedModule != null) {
            result.add(usedModule);
        }

        return result;
    };

    @Override
    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    @Override
    public void inject(Object target) {
        getObjectGraph().inject(target);
    }
}
