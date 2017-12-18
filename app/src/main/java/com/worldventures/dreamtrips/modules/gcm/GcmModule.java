package com.worldventures.dreamtrips.modules.gcm;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.techery.spares.utils.gson.LowercaseEnumTypeAdapterFactory;
import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.auth.api.command.LogoutAction;
import com.worldventures.core.utils.DateTimeDeserializer;
import com.worldventures.core.utils.DateTimeSerializer;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;
import com.worldventures.dreamtrips.modules.gcm.command.UnsubscribeFromPushCommand;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDataParser;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationFactoryHolder;
import com.worldventures.dreamtrips.modules.gcm.service.PushListenerService;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;
import com.worldventures.dreamtrips.modules.gcm.service.SubscribeToPushNotificationsInteractor;

import java.util.Date;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {RegistrationIntentService.class, PushListenerService.class},
      includes = {NotificationFactoryModule.class},
      complete = false,
      library = true)
public class GcmModule {

   private static final String GCM_ONLY = "GcmModule";

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
   NotificationDataParser provideDataParser(@Named(GCM_ONLY) Gson gson) {
      return new NotificationDataParser(gson);
   }

   @Provides
   @Named(GCM_ONLY)
   Gson provideGson() {
      return new GsonBuilder().serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory("unknown"))
            .registerTypeAdapter(Date.class, new DateTimeDeserializer())
            .registerTypeAdapter(Date.class, new DateTimeSerializer())
            .create();
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
