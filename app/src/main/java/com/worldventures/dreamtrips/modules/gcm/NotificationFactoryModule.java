package com.worldventures.dreamtrips.modules.gcm;

import android.content.Context;

import com.messenger.notification.MessengerNotificationFactory;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.friends.notification.FriendNotificationFactory;
import com.worldventures.dreamtrips.modules.gcm.delegate.MerchantNotficationFactory;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationFactoryHolder;
import com.worldventures.dreamtrips.modules.gcm.delegate.PhotoNotificationFactory;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(
      complete = false,
      library = true)
public class NotificationFactoryModule {

   @Provides
   NotificationFactoryHolder provideNotificationFactoryHolder(FriendNotificationFactory friendNotificationFactory,
         PhotoNotificationFactory photoNotificationFactory, MessengerNotificationFactory messengerNotificationFactory,
         MerchantNotficationFactory merchantNotficationFactory) {
      return new NotificationFactoryHolder(friendNotificationFactory, photoNotificationFactory, messengerNotificationFactory, merchantNotficationFactory);
   }

   @Provides
   FriendNotificationFactory provideFriendNotificationFactory(@ForApplication Context context, @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator) {
      return new FriendNotificationFactory(context, routeCreator);
   }

   @Provides
   PhotoNotificationFactory providePhotoNotificationFactory(@ForApplication Context context) {
      return new PhotoNotificationFactory(context);
   }

   @Provides
   MerchantNotficationFactory provideMerchantNotficationFactory(@ForApplication Context context) {
      return new MerchantNotficationFactory(context);
   }

   @Provides
   MessengerNotificationFactory provideMessengerNotificationFactory(@ForApplication Context context) {
      return new MessengerNotificationFactory(context);
   }
}
