package com.worldventures.dreamtrips.modules.trips;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
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

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideTripsComponent() {
      return new ComponentDescription(TRIPS, R.string.trips, R.string.trips, R.drawable.ic_dreamtrips, TripListFragment.class);
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideMapTripsComponent() {
      return new ComponentDescription(MAP_TRIPS, R.string.trips, R.string.trips, R.drawable.ic_dreamtrips, true, true, TripMapFragment.class);
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideOTAComponent() {
      return new ComponentDescription(OTA, R.string.other_travel, R.string.other_travel, R.drawable.ic_other_travel, OtaFragment.class);
   }
}
