package com.worldventures.dreamtrips;

import android.app.Application;

import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.module.ProdAppModule;

import dagger.ObjectGraph;

public class DTApplication extends Application {

    private DataManager dataManager;
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        Object prodModule = new ProdAppModule();
        objectGraph = ObjectGraph.create(prodModule);
        dataManager = new DataManager(this);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }
}
