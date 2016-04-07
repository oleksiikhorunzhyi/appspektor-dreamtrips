package com.worldventures.dreamtrips.modules.dtl;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlFiltersPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsSearchPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapInfoPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantDetailsPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantListPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantsHostPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantsTabsPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanQrCodePresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlStartPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlVerifyAmountPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlFilterAttributeCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantCell;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DtlPointsEstimationFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlFiltersFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlImageFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlLocationsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlLocationsSearchFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMapFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMapInfoFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantDetailsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsHostFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsListFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsTabsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanQrCodeFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlStartFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlTransactionSucceedFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlVerifyAmountFragment;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DtlFilterAttributeHeaderCell;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DtlStartFragment.class,
                DtlStartPresenter.class,
                DtlMerchantsHostFragment.class,
                DtlMerchantsHostPresenter.class,
                DtlLocationsPresenter.class,
                DtlLocationsSearchPresenter.class,
                DtlLocationsFragment.class,
                DtlLocationsSearchFragment.class,
                DtlLocationCell.class,
                DtlFilterAttributeCell.class,
                DtlFilterAttributeHeaderCell.class,
                DtlMapFragment.class,
                DtlMapPresenter.class,
                DtlMapInfoFragment.class,
                DtlMapInfoPresenter.class,
                DtlMerchantsTabsFragment.class,
                DtlMerchantsTabsPresenter.class,
                DtlScanReceiptFragment.class,
                DtlScanReceiptPresenter.class,
                DtlMerchantsListFragment.class,
                DtlMerchantListPresenter.class,
                DtlMerchantCell.class,
                DtlMerchantDetailsPresenter.class,
                DtlMerchantDetailsFragment.class,
                DtlPointsEstimationFragment.class,
                DtlPointsEstimationPresenter.class,
                DtlFiltersFragment.class,
                DtlFiltersPresenter.class,

                DtlMerchantDetailsPresenter.class,
                DtlMerchantDetailsFragment.class,

                DtlScanQrCodeFragment.class,
                DtlScanQrCodePresenter.class,
                DtlTransactionSucceedFragment.class,
                DtlTransactionSucceedPresenter.class,
                DtlMerchantCell.class,

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

}
