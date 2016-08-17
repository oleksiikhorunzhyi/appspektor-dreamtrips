package com.worldventures.dreamtrips.core.module;

import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(library = true, complete = false)
public class HolderModule {

   @Provides
   @Singleton
   public SessionHolder<UserSession> session(SimpleKeyValueStorage simpleKeyValueStorage, @Global EventBus eventBus) {
      return new SessionHolder<>(simpleKeyValueStorage, UserSession.class, eventBus);
   }

   @Provides
   @Singleton
   public FeatureManager featureManager(SessionHolder<UserSession> session) {
      return new FeatureManager(session);
   }
}
