package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.filter.DtlFilterPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsScreenImpl;
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
                DtlMapInfoPresenterImpl.class,
                DtlMapInfoScreenImpl.class,
                DtlStartPresenterImpl.class,
                DtlStartScreenImpl.class,
                DtlMerchantsPresenterImpl.class,
                DtlMerchantsScreenImpl.class,
                DtlDetailsPresenterImpl.class,
                DtlDetailsScreenImpl.class,
                ActivityPresenter.class,
                DtlFilterPresenterImpl.class
        },
        complete = false, library = true
)
public class DtlActivityModule {

    public static final String DTLFLOW = "DTLFLOW";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideDtlComponent() {
        return new ComponentDescription(DTLFLOW, R.string.dtl_withflow, R.string.dtl_withflow, R.drawable.ic_dtl,
                true, null);
    }
}
