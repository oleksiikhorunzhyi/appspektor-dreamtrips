package com.worldventures.dreamtrips.core.module;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class HolderModule {

   @Provides
   @Singleton
   public SessionHolder<UserSession> session(SimpleKeyValueStorage simpleKeyValueStorage) {
      return new SessionHolder<>(simpleKeyValueStorage, UserSession.class);
   }

   @Provides
   @Singleton
   public FeatureManager featureManager(SessionHolder<UserSession> session) {
      return new FeatureManager(session);
   }
}
