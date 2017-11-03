package com.worldventures.dreamtrips.modules.gcm;

import android.content.Context;

import com.messenger.notification.MessengerNotificationFactory;
import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.module.FragmentClassProviderModule;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.modules.gcm.delegate.MerchantNotficationFactory;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationFactoryHolder;
import com.worldventures.dreamtrips.modules.gcm.delegate.PhotoNotificationFactory;
import com.worldventures.dreamtrips.social.ui.friends.notification.FriendNotificationFactory;

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
   FriendNotificationFactory provideFriendNotificationFactory(@ForApplication Context context, @Named(FragmentClassProviderModule.PROFILE) FragmentClassProvider<Integer> fragmentClassProvider) {
      return new FriendNotificationFactory(context, fragmentClassProvider);
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
