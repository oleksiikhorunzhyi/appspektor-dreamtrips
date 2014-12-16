package com.worldventures.dreamtrips;

import android.app.Application;
import android.content.Context;

import com.worldventures.dreamtrips.core.module.DTModule;
import com.worldventures.dreamtrips.utils.Logs;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class DTApplication extends Application {

    public static final String DEFAULT_TAG = "DreamApp";
    private ObjectGraph objectGraph;

    public static DTApplication get(Context context) {
        return (DTApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initGraph();
        Logs.init(DEFAULT_TAG, BuildConfig.DEBUG);

    }

    private void initGraph() {
        objectGraph = ObjectGraph.create(getModules().toArray());
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new DTModule(this));
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }

    public ObjectGraph plus(Object... obj) {
        return objectGraph.plus(obj);
    }
}
