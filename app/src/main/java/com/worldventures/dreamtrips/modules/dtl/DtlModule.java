package com.worldventures.dreamtrips.modules.dtl;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesListPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesTabsPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesListFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesTabsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlLocationsFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DtlLocationsPresenter.class,
                DtlLocationsFragment.class,
                DtlLocationCell.class,
                DtlPlacesTabsFragment.class,
                DtlPlacesTabsPresenter.class,
                DtlPlacesListFragment.class,
                DtlPlacesListPresenter.class
        },
        complete = false,
        library = true
)
public class DtlModule {

    public static final String DTL = Route.DTL_LOCATIONS.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideDtlComponent() {
        return new ComponentDescription(DTL, R.string.dtl, R.string.dtl, R.drawable.ic_dtl, DtlPlacesTabsFragment.class);
    }
}
