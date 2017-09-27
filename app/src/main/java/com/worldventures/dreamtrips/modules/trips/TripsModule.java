package com.worldventures.dreamtrips.modules.trips;

import com.worldventures.dreamtrips.core.ui.fragment.BaseImageFragment;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImagePresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.OtaPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.trips.presenter.FiltersPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.TripDetailsPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.TripListPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapListPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapPresenter;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DateCell;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FiltersFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripDetailsFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripListFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapListFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripViewPagerFragment;

import dagger.Module;

@Module(
      injects = {
            TripListPresenter.class,
            FiltersPresenter.class,
            TripDetailsFragment.class,
            TripDetailsPresenter.class,
            FiltersFragment.class,
            TripListFragment.class,
            DateCell.class,
            TripMapFragment.class,
            TripMapPresenter.class,
            TripViewPagerFragment.class,
            TripMapListFragment.class,
            TripMapListPresenter.class,
            BaseImageFragment.class,
            BaseImagePresenter.class,
            OtaFragment.class,
            OtaPresenter.class,
      },
      complete = false,
      library = true)
public class TripsModule {
}
