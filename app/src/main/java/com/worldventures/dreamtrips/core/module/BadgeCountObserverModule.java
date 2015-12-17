package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.utils.BadgeCountObserver;
import com.worldventures.dreamtrips.core.initializer.BadgeCountObserverInitializer;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.BadgeUpdater;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import me.leolin.shortcutbadger.ShortcutBadger;

@Module(injects = {
            BadgeCountObserverInitializer.class
        },
        library = true,
        complete = false)
public class BadgeCountObserverModule {

    @Provides
    @Singleton
    BadgeCountObserver provideBadgeCountObserver(BadgeUpdater badgeUpdater, @Global EventBus bus, SnappyRepository repository){
        return new BadgeCountObserver(badgeUpdater, repository, bus);
    }

    @Provides
    @Singleton
    BadgeUpdater provideBadgeUpdater(@ForApplication Context context) {
        return count -> ShortcutBadger.with(context).count(count);
    }

}
