package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;

import javax.inject.Inject;


public class DtlInitializer implements AppInitializer {

    @Inject
    protected SnappyRepository db;

    @Inject
    protected DtlLocationManager locationManager;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        // initialize dtl 
    }
}
