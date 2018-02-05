package com.worldventures.dreamtrips.modules.trips;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.modules.trips.command.CheckTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetRegionsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsLocationsCommand;
import com.worldventures.dreamtrips.modules.trips.model.converter.ActivityConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.ContentItemConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.RegionConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripPinToPinConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripWithDetailsToTripConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripWithoutDetailsToTripConverter;

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
