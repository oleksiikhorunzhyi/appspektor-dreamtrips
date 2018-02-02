package com.worldventures.dreamtrips.modules.trips;

import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.service.storage.ActivitiesStorage;
import com.worldventures.dreamtrips.modules.trips.service.storage.RegionsStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripDetailsStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripPinsStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripsByUidsStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripsDiskStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripsFiltersStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripsStorage;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.janet.cache.storage.MemoryStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class TripsStorageModule {

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideActivitiesStorage() {
      return new ActivitiesStorage(new MemoryStorage<>());
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideRegionsStorage() {
      return new RegionsStorage(new MemoryStorage<>());
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTripsStorage(SnappyRepository snappyRepository) {
      return new TripsStorage(new PaginatedMemoryStorage<>(), new TripsDiskStorage(snappyRepository));
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTripsFiltersStorage(SnappyRepository snappyRepository) {
      return new TripsFiltersStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTripsPinsStorage(SnappyRepository snappyRepository) {
      return new TripPinsStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTripsDetailsStorage(SnappyRepository snappyRepository) {
      return new TripsByUidsStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTripDetailsStorage(SnappyRepository snappyRepository) {
      return new TripDetailsStorage(snappyRepository);
   }

}
