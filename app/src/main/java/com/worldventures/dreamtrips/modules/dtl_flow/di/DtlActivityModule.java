package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.start.DtlStartPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.start.DtlStartScreenImpl;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DtlLocationsSearchScreenImpl.class,
                DtlLocationsSearchPresenterImpl.class,
                DtlLocationsScreenImpl.class,
                DtlLocationsPresenterImpl.class,
                DtlMapPresenterImpl.class,
                DtlMapScreenImpl.class,
                DtlStartPresenterImpl.class,
                DtlStartScreenImpl.class,
                ActivityPresenter.class,
        },
        complete = false, library = true
)
public class DtlActivityModule {

    public static final String DTLFLOW = "DTLFLOW";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideDtlComponent() {
        return new ComponentDescription(DTLFLOW, R.string.dtl, R.string.dtl, R.drawable.ic_dtl,
                true, null);
    }
}
