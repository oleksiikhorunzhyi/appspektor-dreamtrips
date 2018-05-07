package com.worldventures.dreamtrips.modules.trips;

import com.worldventures.core.converter.Converter;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.trips.model.converter.ActivityConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.ContentItemConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.RegionConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripPinToPinConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripWithDetailsToTripConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripWithoutDetailsToTripConverter;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.modules.trips.service.command.CheckTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.service.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.service.command.GetRegionsCommand;
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsCommand;
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsLocationsCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
      GetActivitiesCommand.class,
      GetRegionsCommand.class,
      CheckTripsByUidCommand.class,
      GetTripsCommand.class,
      GetTripDetailsCommand.class,
      GetTripsLocationsCommand.class,
      GetTripsByUidCommand.class,
}, complete = false, library = true)
public class TripsJanetCommandModule {

   @Provides
   @Singleton
   TripsInteractor provideTripsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new TripsInteractor(sessionActionPipeCreator);
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePinConverter() {
      return new TripPinToPinConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideTripConverter() {
      return new TripWithoutDetailsToTripConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideTripWithDetailsConverter() {
      return new TripWithDetailsToTripConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideActivityConverter() {
      return new ActivityConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideRegionConverter() {
      return new RegionConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideContentItemConverter() {
      return new ContentItemConverter();
   }
}
