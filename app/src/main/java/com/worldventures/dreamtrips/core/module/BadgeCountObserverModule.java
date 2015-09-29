package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.utils.BadgeCountObserver;
import com.worldventures.dreamtrips.core.initializer.BadgeCountObserverInitializer;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(injects = {
            BadgeCountObserverInitializer.class
        },
        library = true,
        complete = false)
public class BadgeCountObserverModule {

    @Provides
    @Singleton
    BadgeCountObserver provideBadgeCountObserver(@Global EventBus bus, SnappyRepository repository, Context context){
        return new BadgeCountObserver(context, repository, bus);
    }

}
