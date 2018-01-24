package com.worldventures.dreamtrips.modules.trips;

import android.support.annotation.DrawableRes;

import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.delegate.ResetFilterEventDelegate;
import com.worldventures.dreamtrips.modules.trips.delegate.TripFilterEventDelegate;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripListFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {
            TripsJanetCommandModule.class,
            TripsStorageModule.class,
      }, library = true, complete = false)
public class TripsAppModule {

   public static final String TRIPS = "TRIPS";
   public static final String MAP_TRIPS = "MAP_TRIPS";

   public static final String MALAYSIYA_COUNTRY_CODE = "my";

   @Provides
   @Singleton
   TripFilterEventDelegate provideTripFilterEventDelegate(SnappyRepository snappyRepository) {
      return new TripFilterEventDelegate(snappyRepository);
   }

   @Provides
   @Singleton
   ResetFilterEventDelegate provideResetFilterEventDelegate() {
      return new ResetFilterEventDelegate();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideTripsComponent(SessionHolder sessionHolder) {
      return new ComponentDescription.Builder()
            .key(TRIPS)
            .toolbarLogo(getLogo(sessionHolder))
            .navMenuTitle(R.string.trips)
            .icon(R.drawable.ic_dreamtrips)
            .fragmentClass(TripListFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideMapTripsComponent(SessionHolder sessionHolder) {
      return new ComponentDescription.Builder()
            .key(MAP_TRIPS)
            .toolbarLogo(getLogo(sessionHolder))
            .navMenuTitle(R.string.trips)
            .icon(R.drawable.ic_dreamtrips)
            .ignored(true)
            .skipGeneralToolbar(true)
            .fragmentClass(TripMapFragment.class)
            .build();
   }

   @DrawableRes
   private int getLogo(SessionHolder sessionHolder) {
      Optional<UserSession> sessionOptional = sessionHolder.get();
      if (sessionOptional.isPresent()) {
         String countryCode = sessionOptional.get().user().getCountryCode();
         if (MALAYSIYA_COUNTRY_CODE.equalsIgnoreCase(countryCode)) {
            return R.drawable.dt_action_bar_logo_skyzone;
         }
      }
      return R.drawable.dt_action_bar_logo;
   }
}
