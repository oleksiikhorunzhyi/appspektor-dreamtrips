package com.worldventures.dreamtrips.modules.dtl;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlFiltersPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlaceDetailsPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesListPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesTabsPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanQrCodePresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlStartPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.DtlPlaceDetailsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlHeaderCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlPlaceCell;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DtlPointsEstimationFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlFiltersFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlLocationsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMapFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesListFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesTabsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanQrCodeFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlStartFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlTransactionSucceedFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DtlStartFragment.class,
                DtlStartPresenter.class,
                DtlLocationsPresenter.class,
                DtlLocationsFragment.class,
                DtlLocationCell.class,
                DtlMapFragment.class,
                DtlMapPresenter.class,
                DtlPlacesTabsFragment.class,
                DtlPlacesTabsPresenter.class,
                DtlScanReceiptFragment.class,
                DtlScanReceiptPresenter.class,
                DtlPlacesListFragment.class,
                DtlPlacesListPresenter.class,
                DtlPlaceCell.class,
                DtlHeaderCell.class,
                DtlPlaceDetailsPresenter.class,
                DtlPlaceDetailsFragment.class,
                DtlPointsEstimationFragment.class,
                DtlPointsEstimationPresenter.class,
                DtlFiltersFragment.class,
                DtlFiltersPresenter.class,

                DtlPlaceDetailsPresenter.class,
                DtlPlaceDetailsFragment.class,

                DtlScanQrCodeFragment.class,
                DtlScanQrCodePresenter.class,
                DtlTransactionSucceedFragment.class,
                DtlTransactionSucceedPresenter.class,
                DtlPlaceCell.class
        },
        complete = false,
        library = true
)
public class DtlModule {

    public static final String DTL = Route.DTL_START.name();

    //TODO replace it after MVP
    public static final double LAT = 33.499561d;
    public static final double LNG = -86.802372d;

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideDtlComponent() {
        return new ComponentDescription(DTL, R.string.dtl, R.string.dtl, R.drawable.ic_dtl, DtlStartFragment.class);
    }

}
