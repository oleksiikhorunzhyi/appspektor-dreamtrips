package com.worldventures.dreamtrips.modules.gcm;

import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;

import dagger.Module;

@Module(
        injects = {
                RegistrationIntentService.class
        },
        complete = false,
        library = true
)
public class GcmModule {

}
