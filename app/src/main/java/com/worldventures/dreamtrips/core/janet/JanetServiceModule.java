package com.worldventures.dreamtrips.core.janet;

import com.worldventures.dreamtrips.core.api.uploadery.UploadImageAction;

import dagger.Module;

@Module(injects = {
        DreamTripsHttpService.class,
        UploadImageAction.class
}, library = true, complete = false)
public class JanetServiceModule {
}
