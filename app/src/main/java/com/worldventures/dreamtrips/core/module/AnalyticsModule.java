package com.worldventures.dreamtrips.core.module;

import android.app.Application;

import com.worldventures.dreamtrips.core.janet.DreamTripsCommandServiceWrapper;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.utils.AnalyticsInteractorProxy;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Tracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.command.ClearHeadersCommand;
import com.worldventures.dreamtrips.core.utils.tracksystem.command.SetUserIdsHeadersCommand;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.GlobalAnalyticEventHandler;
import com.worldventures.dreamtrips.modules.common.delegate.system.ConnectionInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false, injects = {
      ClearHeadersCommand.class,
      SetUserIdsHeadersCommand.class
})
public class AnalyticsModule {

   public static final String ADOBE_TRACKER = "Adobe";

   @Singleton
   @Provides
   @Named(ADOBE_TRACKER)
   Tracker provideAdobeTracker(ConnectionInfoProvider connectionInfoProvider, DeviceInfoProvider deviceInfoProvider) {
      return new AdobeTracker(deviceInfoProvider, connectionInfoProvider);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   Tracker provideAdobeTrackerForSet(@Named(ADOBE_TRACKER) Tracker adobeTracker) {
      return adobeTracker;
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   Tracker provideApptentiveTracker(Application application) {
      return new ApptentiveTracker(application);
   }

   @Singleton
   @Provides
   AnalyticsInteractor provideAnalyticsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new AnalyticsInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   GlobalAnalyticEventHandler provideGlobalAnalyticEventHandler(AnalyticsInteractor analyticsInteractor, CachedEntityInteractor interactor,
         DreamTripsCommandServiceWrapper commandServiceWrapper) {
      return new GlobalAnalyticEventHandler(analyticsInteractor, interactor, commandServiceWrapper);
   }

   @Provides
   AnalyticsInteractorProxy provideAnalyticsInteractorProxy(AnalyticsInteractor analyticsInteractor) {
      return new AnalyticsInteractorProxy(analyticsInteractor);
   }
}
