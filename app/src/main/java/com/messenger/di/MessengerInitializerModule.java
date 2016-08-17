package com.messenger.di;

import android.content.Context;

import com.messenger.initializer.ChatFacadeInitializer;
import com.messenger.initializer.FailedMessageInitializer;
import com.messenger.initializer.MessengerInitializer;
import com.messenger.initializer.PresenceListenerInitializer;
import com.messenger.initializer.RosterListenerInitializer;
import com.messenger.initializer.StorageInitializer;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.qualifier.ForApplication;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {MessengerInitializer.class, FailedMessageInitializer.class, RosterListenerInitializer.class, StorageInitializer.class, PresenceListenerInitializer.class, ChatFacadeInitializer.class},
      complete = false, library = true)
public class MessengerInitializerModule {

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideStorageInitializer(@ForApplication Context context) {
      return new StorageInitializer(context);
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideFacadeInitializer() {
      return new ChatFacadeInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideMessengerConnectorInitializer() {
      return new MessengerInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer providePresenceListenerInitializer() {
      return new PresenceListenerInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideRosterInitializer() {
      return new RosterListenerInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideFailedMessageInitializer() {
      return new FailedMessageInitializer();
   }
}
