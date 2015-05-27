package com.worldventures.dreamtrips.modules.trips;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.trips.presenter.BookItActivityPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.DetailTripActivityPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.DetailedTripPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.DreamTripsFragmentPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.FiltersPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.MapTripInfoPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.MapFragmentPresenter;
import com.worldventures.dreamtrips.modules.trips.view.activity.BookItActivity;
import com.worldventures.dreamtrips.modules.trips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.HeaderRegionCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.HeaderThemeCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.RecentlyAddedCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.ThemeCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DateCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.FavoritesCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.FilterRangeBarsCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.RegionCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.SoldOutCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;
import com.worldventures.dreamtrips.modules.trips.view.fragment.DetailedTripFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.DreamTripsFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FiltersFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.MapTripInfoFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.MapFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                BookItActivity.class,
                DreamTripsFragmentPresenter.class,
                FiltersPresenter.class,
                BookItActivityPresenter.class,
                DetailedTripFragment.class,
                DetailTripActivity.class,
                DetailTripActivityPresenter.class,
                DetailedTripPresenter.class,
                MapTripInfoPresenter.class,
                MapFragmentPresenter.class,
                FiltersFragment.class,
                MapTripInfoFragment.class,
                DreamTripsFragment.class,
                MapFragment.class,

                FilterRangeBarsCell.class,
                ThemeCell.class,
                HeaderThemeCell.class,
                SoldOutCell.class,
                DateCell.class,
                RegionCell.class,
                TripCell.class,
                FavoritesCell.class,
                HeaderRegionCell.class,
                RecentlyAddedCell.class
        },
        complete = false,
        library = true
)
public class TripsModule {

    public static final String TRIPS = Route.DREAMTRIPS.name();
    public static final String OTA = Route.OTA.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideTripsComponent() {
        return new ComponentDescription(TRIPS, R.string.trips, R.drawable.ic_dreamtrips, DreamTripsFragment.class);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideOTAComponent() {
        return new ComponentDescription(OTA, R.string.other_travel, R.drawable.ic_other_travel, OtaFragment.class);
    }
}
