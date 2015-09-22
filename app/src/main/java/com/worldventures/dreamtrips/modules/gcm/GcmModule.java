package com.worldventures.dreamtrips.modules.gcm;

import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.gcm.service.PushListenerService;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;

import dagger.Module;

@Module(
        injects = {
                RegistrationIntentService.class,
                PushListenerService.class,
                NotificationDelegate.class,
        },
        complete = false,
        library = true
)
public class GcmModule {

}
