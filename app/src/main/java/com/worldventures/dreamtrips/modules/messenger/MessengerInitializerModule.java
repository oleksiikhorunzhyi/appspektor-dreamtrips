package com.worldventures.dreamtrips.modules.messenger;

import com.messenger.di.MessengerInitializer;
import com.messenger.di.UnhandledMessageWatcher;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.ui.inappnotifications.AppNotification;
import com.techery.spares.application.AppInitializer;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MessengerInitializer.class
        },
        complete = false, library = true
)
public class MessengerInitializerModule {

    @Provides(type = Provides.Type.SET)
    public AppInitializer provideMessengerConnectorInitializer() {
        return new MessengerInitializer();
    }

    @Provides
    UnhandledMessageWatcher provideUnhandledMessageWatcher(MessengerServerFacade messengerServerFacade, AppNotification appNotification) {
        return new UnhandledMessageWatcher(messengerServerFacade, appNotification);
    }
}
