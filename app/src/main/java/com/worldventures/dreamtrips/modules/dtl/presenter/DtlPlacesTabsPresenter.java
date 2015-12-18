package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlSearchDelegate;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;
import com.worldventures.dreamtrips.modules.dtl.event.PlaceClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesListFragment;

import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class DtlPlacesTabsPresenter extends Presenter<DtlPlacesTabsPresenter.View> {

    @Inject
    SnappyRepository db;
    @Inject
    DtlMerchantRepository dtlMerchantRepository;
    @Inject
    DtlSearchDelegate dtlSearchDelegate;
    //
    @State
    boolean initialized;

    private DtlLocation location;

    public DtlPlacesTabsPresenter(@Nullable DtlLocation location) {
        if (location == null) location = db.getSelectedDtlLocation();
        this.location = location;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        dtlMerchantRepository.setRequestingPresenter(this);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        view.initToolbar(location);
        setTabs();

        if (!initialized)
            loadPlaces();

        initialized = true;
    }

    public void applySearch(String query) {
        dtlSearchDelegate.applySearch(query);
    }

    private void loadPlaces() {
        dtlMerchantRepository.loadMerchants(location);
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        dtlMerchantRepository.onMerchantLoadingError(error);
    }

    public void setTabs() {
        view.setTypes(dtlMerchantRepository.getDtlMerchantTypes());
        view.updateSelection();
    }

    public Bundle prepareArgsForTab(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DtlPlacesListFragment.EXTRA_TYPE, dtlMerchantRepository.getDtlMerchantTypes().get(position));
        return bundle;
    }

    /**
     * Analytics-related
     */
    public void trackTabChange(int newPosition) {
        String newTabName = dtlMerchantRepository.getDtlMerchantTypes().get(newPosition).equals(DtlMerchantType.OFFER) ?
                TrackingHelper.DTL_ACTION_OFFERS_TAB : TrackingHelper.DTL_ACTION_DINING_TAB;
        TrackingHelper.dtlPlacesTab(newTabName);
    }

    public void onEventMainThread(final PlaceClickedEvent event) {
        if (!view.isTabletLandscape()) {
            view.openDetails(event.getMerchantId());
        }
    }

    @Override
    public void dropView() {
        dtlMerchantRepository.detachRequestingPresenter();
        super.dropView();
    }

    public interface View extends ApiErrorView {

        void setTypes(List<DtlMerchantType> types);

        void updateSelection();

        void initToolbar(DtlLocation location);

        void openDetails(String merchantId);
    }
}
