package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.techery.spares.service.ServiceActionRunner;
import com.worldventures.dreamtrips.core.uploader.UploadingService;

import javax.inject.Inject;

public class UploadingServiceInitializer implements AppInitializer {

    @Inject
    ServiceActionRunner serviceActionRunner;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);

        serviceActionRunner.from(UploadingService.class).start();

//        UploadingService.ImageUploadAction action = new UploadingService.ImageUploadAction("content://media/external/file/91");

//        serviceActionRunner.from(UploadingService.class).run(action);
    }
}
