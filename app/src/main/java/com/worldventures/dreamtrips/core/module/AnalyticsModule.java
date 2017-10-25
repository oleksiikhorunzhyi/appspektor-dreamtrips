package com.worldventures.dreamtrips.core.module;

import android.app.Application;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.service.CachedEntityInteractor;
import com.worldventures.core.service.ConnectionInfoProvider;
import com.worldventures.core.service.DeviceInfoProvider;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.Tracker;
import com.worldventures.core.service.analytics.TrackerQualifier;
import com.worldventures.core.service.analytics.command.ClearHeadersCommand;
import com.worldventures.core.service.analytics.command.SetUserIdsHeadersCommand;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.DreamTripsCommandServiceWrapper;
import com.worldventures.dreamtrips.core.utils.AnalyticsInteractorProxy;
import com.worldventures.dreamtrips.modules.common.delegate.GlobalAnalyticEventHandler;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false, injects = {
      ClearHeadersCommand.class,
      SetUserIdsHeadersCommand.class
})
public class AnalyticsModule {

   @Singleton
   @Provides
   @Named(TrackerQualifier.ADOBE_TRACKER)
   Tracker provideAdobeTracker(ConnectionInfoProvider connectionInfoProvider, DeviceInfoProvider deviceInfoProvider) {
      return new AdobeTracker(deviceInfoProvider, connectionInfoProvider, BuildConfig.DEBUG);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   Tracker provideAdobeTrackerForSet(@Named(TrackerQualifier.ADOBE_TRACKER) Tracker adobeTracker) {
      return adobeTracker;
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   Tracker provideApptentiveTracker(Application application) {
      return new ApptentiveTracker(application, BuildConfig.SURVEY_API_TOKEN, BuildConfig.QA_AUTOMATION_MODE_ENABLED);
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
