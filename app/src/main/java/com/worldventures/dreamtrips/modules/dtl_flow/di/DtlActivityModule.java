package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.techery.spares.adapter.expandable.BaseExpandableAdapter;
import com.techery.spares.adapter.expandable.BaseExpandableDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanQrCodePresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlVerifyAmountPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlFilterAttributeCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantExpandableCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlPerkCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlPointsCell;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DtlPointsEstimationFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanQrCodeFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlTransactionSucceedFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlVerifyAmountFragment;
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
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DtlFilterAttributeHeaderCell;

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
                DtlFilterPresenterImpl.class,
                DtlLocationCell.class,
                DtlFilterAttributeCell.class,
                DtlFilterAttributeHeaderCell.class,
                DtlMerchantExpandableCell.class,
                DtlPerkCell.class,
                DtlPointsCell.class,
                DtlScanReceiptFragment.class,
                DtlScanReceiptPresenter.class,
                DtlPointsEstimationFragment.class,
                DtlPointsEstimationPresenter.class,
                DtlScanQrCodeFragment.class,
                DtlScanQrCodePresenter.class,
                DtlTransactionSucceedFragment.class,
                DtlTransactionSucceedPresenter.class,
                DtlVerifyAmountFragment.class,
                DtlVerifyAmountPresenter.class,
                BaseExpandableAdapter.class,
                BaseExpandableDelegateAdapter.class,
        },
        complete = false, library = true
)
public class DtlActivityModule {

    public static final String DTL = "DTL";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideDtlComponent() {
        return new ComponentDescription(DTL, R.string.dtl, R.string.dtl, R.drawable.ic_dtl, true,
                null);
    }
}
