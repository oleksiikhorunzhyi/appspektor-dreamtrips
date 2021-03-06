package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.modules.auth.api.command.LogoutAction;
import com.worldventures.dreamtrips.core.initializer.BadgeCountObserverInitializer;
import com.worldventures.dreamtrips.core.utils.BadgeUpdater;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.leolin.shortcutbadger.ShortcutBadger;

@Module(injects = {BadgeCountObserverInitializer.class},
        library = true,
        complete = false)
public class BadgeCountObserverModule {

   @Provides
   @Singleton
   BadgeUpdater provideBadgeUpdater(@ForApplication Context context) {
      return count -> ShortcutBadger.with(context).count(count);
   }

   @Provides(type = Provides.Type.SET)
   LogoutAction provideBadgeUpdaterLogoutAction(BadgeUpdater badgeUpdater) {
      return () -> badgeUpdater.updateBadge(0);
   }

}
