package com.worldventures.core.modules;


import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.utils.BadgeHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class UtilModule {

   @Provides
   @Singleton
   BadgeHelper provideBadgeManager(SessionHolder sessionHolder) {
      return new BadgeHelper(sessionHolder);
   }
}

