package com.worldventures.dreamtrips.modules.gcm;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDataParser;
import com.worldventures.dreamtrips.modules.gcm.service.PushListenerService;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                RegistrationIntentService.class,
                PushListenerService.class
        },
        complete = false,
        library = true
)
public class GcmModule {

    @Provides
    NotificationDataParser provideDataParser() {
        return new NotificationDataParser();
    }

    @Provides
    NotificationDelegate provideNotificationDelegate(@ForApplication Context context, @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator) {
        return new NotificationDelegate(context, routeCreator);
    }

}
