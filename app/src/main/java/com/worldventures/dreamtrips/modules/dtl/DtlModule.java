package com.worldventures.dreamtrips.modules.dtl;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.dtl.presenter.LocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.LocationsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.PlacesTabsFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                LocationsPresenter.class,
                LocationsFragment.class,
                DtlLocationCell.class,
                PlacesTabsFragment.class
        },
        complete = false,
        library = true
)
public class DtlModule {

    public static final String DTL = Route.DTL_LOCATIONS.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideDtlComponent() {
        return new ComponentDescription(DTL, R.string.dtl, R.string.dtl, R.drawable.ic_dtl, PlacesTabsFragment.class);
    }
}
