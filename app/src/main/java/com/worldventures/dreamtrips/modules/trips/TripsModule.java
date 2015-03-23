package com.worldventures.dreamtrips.modules.trips;

import com.worldventures.dreamtrips.modules.trips.presenter.BookItActivityPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.BookItDialogPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.DetailTripActivityPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.DetailedTripPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.DreamTripsFragmentPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.FiltersPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.FragmentMapInfoPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.MapFragmentPresenter;
import com.worldventures.dreamtrips.modules.trips.view.activity.BookItActivity;
import com.worldventures.dreamtrips.modules.trips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.modules.trips.view.cell.ActivityCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.DateCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.FiltersCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.RegionCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.SoldOutCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.ThemeHeaderCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;
import com.worldventures.dreamtrips.modules.trips.view.dialog.BookItDialogFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.DetailedTripFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.DreamTripsFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FiltersFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FragmentMapTripInfo;
import com.worldventures.dreamtrips.modules.trips.view.fragment.MapFragment;

import dagger.Module;

@Module(
        injects = {
                BookItActivity.class,
                DreamTripsFragmentPresenter.class,
                FiltersPresenter.class,
                BookItDialogPresenter.class,
                BookItActivityPresenter.class,
                BookItDialogFragment.class,
                DetailedTripFragment.class,
                DetailTripActivity.class,
                DetailTripActivityPresenter.class,
                DetailedTripPresenter.class,
                FragmentMapInfoPresenter.class,
                MapFragmentPresenter.class,
                FiltersFragment.class,
                FragmentMapTripInfo.class,
                DreamTripsFragment.class,
                MapFragment.class,

                FiltersCell.class,
                ActivityCell.class,
                ThemeHeaderCell.class,
                SoldOutCell.class,
                DateCell.class,
                RegionCell.class,
                TripCell.class,
        },
        complete = false,
        library = true
)
public class TripsModule {
}
