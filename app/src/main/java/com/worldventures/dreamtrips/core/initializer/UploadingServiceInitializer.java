package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.uploader.UploadingService;

import javax.inject.Inject;

public class UploadingServiceInitializer implements AppInitializer {

    @Inject
    Context context;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);

        UploadingService.start(context);

        UploadingService.addUploading(context, "content://media/external/file/91");
    }
}
