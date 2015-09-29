package com.worldventures.dreamtrips.modules.gcm;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.friends.notification.FriendNotificationFactory;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDataParser;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.gcm.service.PushListenerService;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
        injects = {
                RegistrationIntentService.class,
                PushListenerService.class
        },
        includes = {
                NotificationFactoryModule.class
        },
        complete = false,
        library = true
)
public class GcmModule {

    @Provides
    NotificationDelegate provideNotificationDelegate(@ForApplication Context context, @Global EventBus bus, SnappyRepository repository, NotificationDataParser dataParser,
                                                     FriendNotificationFactory friendNotificationFactory) {
        return new NotificationDelegate(context, bus, repository, dataParser, friendNotificationFactory);
    }

    @Provides
    NotificationDataParser provideDataParser() {
        return new NotificationDataParser();
    }

}
