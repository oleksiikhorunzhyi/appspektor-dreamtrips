package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Tracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(library = true, complete = false)
public class AnalyticsModule {

   @Singleton
   @Provides(type = Provides.Type.SET)
   Tracker provideAdobeTracker() {
      return new AdobeTracker();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   Tracker provideApptentiveTracker() {
      return new ApptentiveTracker();
   }

   @Singleton
   @Provides
   AnalyticsInteractor provideAnalyticsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new AnalyticsInteractor(sessionActionPipeCreator);
   }
}
