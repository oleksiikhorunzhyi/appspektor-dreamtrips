package com.worldventures.dreamtrips.modules.gcm;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.friends.notification.FriendNotificationFactory;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class NotificationFactoryModule {

    @Provides
    FriendNotificationFactory provideFriendNotificationFactory(@ForApplication Context context, @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator) {
        return new FriendNotificationFactory(context, routeCreator);
    }
}
