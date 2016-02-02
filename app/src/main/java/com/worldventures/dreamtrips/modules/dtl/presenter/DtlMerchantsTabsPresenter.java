package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlSearchDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsListFragment;

import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class DtlMerchantsTabsPresenter extends JobPresenter<DtlMerchantsTabsPresenter.View> {

    @Inject
    DtlMerchantRepository dtlMerchantRepository;
    @Inject
    DtlLocationManager locationRepository;
    @Inject
    DtlSearchDelegate dtlSearchDelegate;
    //
    @State
    boolean initialized;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        view.initToolbar(locationRepository.getCachedSelectedLocation());
        setTabs();
        //
        if (!initialized) loadMerchants();
        initialized = true;
        //
        bindJobCached(dtlMerchantRepository.getMerchantsExecutor)
                .onError(apiErrorPresenter::handleError);
    }

    public void applySearch(String query) {
        dtlSearchDelegate.applySearch(query);
    }

    private void loadMerchants() {
        dtlMerchantRepository.loadMerchants(locationRepository.getCachedSelectedLocation());
    }

    public void setTabs() {
        view.setTypes(dtlMerchantRepository.getDtlMerchantTypes());
        view.updateSelection();
        view.preselectOfferTab(locationRepository.getCachedSelectedLocation().getPartnerCount() > 0);
    }

    public Bundle prepareArgsForTab(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DtlMerchantsListFragment.EXTRA_TYPE, dtlMerchantRepository.getDtlMerchantTypes().get(position));
        return bundle;
    }

    /**
     * Analytics-related
     */
    public void trackTabChange(int newPosition) {
        String newTabName = dtlMerchantRepository.getDtlMerchantTypes().get(newPosition).equals(DtlMerchantType.OFFER) ?
                TrackingHelper.DTL_ACTION_OFFERS_TAB : TrackingHelper.DTL_ACTION_DINING_TAB;
        TrackingHelper.dtlMerchantsTab(newTabName);
    }

    public void onEventMainThread(final MerchantClickedEvent event) {
        if (!view.isTabletLandscape()) {
            view.openDetails(event.getMerchantId());
        }
    }

    public interface View extends RxView, ApiErrorView {

        void setTypes(List<DtlMerchantType> types);

        void updateSelection();

        void initToolbar(DtlLocation location);

        void openDetails(String merchantId);

        void preselectOfferTab(boolean preselectOffer);
    }
}
