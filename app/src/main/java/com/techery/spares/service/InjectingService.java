package com.techery.spares.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.techery.spares.module.Annotations.UseModule;
import com.techery.spares.module.InjectingServiceModule;
import com.techery.spares.module.Injector;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public abstract class InjectingService extends Service implements Injector {
    private ObjectGraph objectGraph;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

        inject(this);
    }

    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();

        result.add(new InjectingServiceModule(this, this));

        Object usedModule = getServiceModule();

        if (usedModule != null) {
            result.add(usedModule);
        }

        return result;
    }

    private Object getServiceModule() {
        try {
            UseModule useModule = this.getClass().getAnnotation(UseModule.class);
            if (useModule != null) {
                return useModule.value().newInstance();
            } else {
                return null;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }
}
