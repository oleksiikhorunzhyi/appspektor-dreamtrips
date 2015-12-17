package com.worldventures.dreamtrips.modules.dtl;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlSearchDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlFiltersPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapInfoPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantListPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlaceDetailsPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesHostPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesTabsPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanQrCodePresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlStartPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlSuggestMerchantPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlVerifyAmountPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlFilterAttributeCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlHeaderCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantCell;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DtlPointsEstimationFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlFiltersFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlImageFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlLocationsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMapFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMapInfoFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlaceDetailsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesHostFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesListFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesTabsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanQrCodeFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlStartFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlSuggestMerchantFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlTransactionSucceedFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlVerifyAmountFragment;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DtlFilterAttributeHeaderCell;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DtlStartFragment.class,
                DtlStartPresenter.class,
                DtlPlacesHostFragment.class,
                DtlPlacesHostPresenter.class,
                DtlLocationsPresenter.class,
                DtlLocationsFragment.class,
                DtlLocationCell.class,
                DtlFilterAttributeCell.class,
                DtlFilterAttributeHeaderCell.class,
                DtlMapFragment.class,
                DtlMapPresenter.class,
                DtlMapInfoFragment.class,
                DtlMapInfoPresenter.class,
                DtlPlacesTabsFragment.class,
                DtlPlacesTabsPresenter.class,
                DtlScanReceiptFragment.class,
                DtlScanReceiptPresenter.class,
                DtlPlacesListFragment.class,
                DtlMerchantListPresenter.class,
                DtlMerchantCell.class,
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
                DtlMerchantCell.class,
                DtlSuggestMerchantFragment.class,
                DtlSuggestMerchantPresenter.class,

                DtlVerifyAmountFragment.class,
                DtlVerifyAmountPresenter.class,

                DtlImageFragment.class,
        },
        complete = false,
        library = true
)
public class DtlModule {

    public static final String DTL = Route.DTL_START.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideDtlComponent() {
        return new ComponentDescription(DTL, R.string.dtl, R.string.dtl, R.drawable.ic_dtl, true,
                DtlStartFragment.class);
    }

    @Provides
    LocationDelegate provideLocationDelegate(@ForApplication Context context) {
        return new LocationDelegate(context);
    }

    @Singleton
    @Provides
    DtlSearchDelegate provideSearchDelegate() {
        return new DtlSearchDelegate();
    }

}
