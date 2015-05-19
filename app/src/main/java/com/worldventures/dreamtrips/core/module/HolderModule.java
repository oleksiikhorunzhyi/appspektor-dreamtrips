package com.worldventures.dreamtrips.core.module;

import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

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
    public LocalesHolder locales(SimpleKeyValueStorage simpleKeyValueStorage) {
        return new LocalesHolder(simpleKeyValueStorage);
    }

    @Provides
    @Singleton
    public StaticPageHolder staticPage(SimpleKeyValueStorage simpleKeyValueStorage) {
        return new StaticPageHolder(simpleKeyValueStorage);
    }
}
