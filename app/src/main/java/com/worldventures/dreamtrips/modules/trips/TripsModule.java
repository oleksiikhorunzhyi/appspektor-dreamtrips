package com.worldventures.dreamtrips.modules.trips;

import android.support.annotation.DrawableRes;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.trips.presenter.FiltersPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.TripDetailsPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.TripListPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapListPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapPresenter;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripMapCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DateCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.FavoritesCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.FilterRangeBarsCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.FilterRecentlyAddedCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.HeaderRegionCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.HeaderThemeCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.RegionCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.SoldOutCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.ThemeCell;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FiltersFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripDetailsFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripListFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapListFragment;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {TripListPresenter.class, FiltersPresenter.class, TripDetailsFragment.class, TripDetailsPresenter.class, FiltersFragment.class, TripListFragment.class,

            FilterRangeBarsCell.class, ThemeCell.class, HeaderThemeCell.class, SoldOutCell.class, DateCell.class, RegionCell.class, TripCell.class, FavoritesCell.class, HeaderRegionCell.class, FilterRecentlyAddedCell.class,

            TripMapFragment.class, TripMapPresenter.class,

            TripMapListFragment.class, TripMapListPresenter.class,

            TripMapCell.class,},
      complete = false,
      library = true)
public class TripsModule {

   public static final String TRIPS = Route.TRIPLIST.name();
   public static final String MAP_TRIPS = Route.MAP.name();
   public static final String OTA = Route.OTA.name();
   public static final String MALAYSIYA_COUNTRY_CODE = "my";

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideTripsComponent(SessionHolder<UserSession> sessionHolder) {
      return new ComponentDescription.Builder()
            .key(TRIPS)
            .toolbarLogo(getLogo(sessionHolder))
            .navMenuTitle(R.string.trips)
            .icon(R.drawable.ic_dreamtrips)
            .fragmentClass(TripListFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideMapTripsComponent(SessionHolder<UserSession> sessionHolder) {
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

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideOTAComponent() {
      return new ComponentDescription.Builder()
            .key(OTA)
            .toolbarTitle(R.string.other_travel)
            .navMenuTitle(R.string.other_travel)
            .icon(R.drawable.ic_other_travel)
            .fragmentClass(OtaFragment.class)
            .build();
   }

   private @DrawableRes
   int getLogo(SessionHolder<UserSession> sessionHolder) {
      String countryCode = sessionHolder.get().get().getUser().getCountryCode();
      if (MALAYSIYA_COUNTRY_CODE.equalsIgnoreCase(countryCode)) {
         return R.drawable.dt_action_bar_logo_skyzone;
      }
      return R.drawable.dt_action_bar_logo;
   }
}
