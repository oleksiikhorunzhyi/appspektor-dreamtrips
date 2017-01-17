package com.worldventures.dreamtrips.core.module;

import android.app.Application;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Tracker;
import com.worldventures.dreamtrips.modules.common.delegate.system.ConnectionInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class AnalyticsModule {

   @Singleton
   @Provides(type = Provides.Type.SET)
   Tracker provideAdobeTracker(ConnectionInfoProvider connectionInfoProvider, DeviceInfoProvider deviceInfoProvider) {
      return new AdobeTracker(deviceInfoProvider, connectionInfoProvider);
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
}
