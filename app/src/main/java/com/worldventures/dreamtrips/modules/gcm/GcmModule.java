package com.worldventures.dreamtrips.modules.gcm;

import android.content.Context;

import com.google.gson.Gson;
import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.auth.api.command.LogoutAction;
import com.worldventures.dreamtrips.modules.gcm.command.UnsubscribeFromPushCommand;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDataParser;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationFactoryHolder;
import com.worldventures.dreamtrips.modules.gcm.service.PushListenerService;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;
import com.worldventures.dreamtrips.modules.gcm.service.SubscribeToPushNotificationsInteractor;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {RegistrationIntentService.class, PushListenerService.class},
      includes = {NotificationFactoryModule.class},
      complete = false,
      library = true)
public class GcmModule {

   @Provides
   @Singleton
   SubscribeToPushNotificationsInteractor subscribeToPushNotificationsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new SubscribeToPushNotificationsInteractor(sessionActionPipeCreator);
   }

   @Provides
   NotificationDelegate provideNotificationDelegate(@ForApplication Context context, UserNotificationInteractor interactor,
         NotificationFactoryHolder notificationFactoryHolder) {
      return new NotificationDelegate(context, interactor, notificationFactoryHolder);
   }

   @Provides
   NotificationDataParser provideDataParser(Gson gson) {
      return new NotificationDataParser(gson);
   }


   @Provides(type = Provides.Type.SET)
   LogoutAction provideNotificationDelegateLogoutction(NotificationDelegate notificationDelegate) {
      return notificationDelegate::cancelAll;
   }

   @Provides(type = Provides.Type.SET)
   LogoutAction providePushNotificationInteractorLogoutAction(SubscribeToPushNotificationsInteractor interactor) {
      return () -> interactor.unsubscribeFromPushCommandActionPipe().send(new UnsubscribeFromPushCommand());
   }

}
