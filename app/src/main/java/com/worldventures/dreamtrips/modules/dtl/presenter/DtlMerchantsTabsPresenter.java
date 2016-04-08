package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsListFragment;

import java.util.List;

import javax.inject.Inject;

public class DtlMerchantsTabsPresenter extends JobPresenter<DtlMerchantsTabsPresenter.View> {

    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlLocationManager dtlLocationManager;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        setTabs();
        //
        if (!view.isTabletLandscape())
            bindJob(dtlMerchantManager.getMerchantsExecutor)
                    .onSuccess(this::tryRetirectToLocation);
        //
        bindJobObservable(dtlMerchantManager.connectMerchantsWithCache())
                .onError(apiErrorPresenter::handleError);
        view.bind(dtlLocationManager.getLocationStream()).compose(new IoToMainComposer<>())
                .subscribe(view::updateToolbarTitle);
    }

    @Override
    public void onResume() {
        super.onResume();
        preselectProperTab();
    }

    private void tryRetirectToLocation(List<DtlMerchant> merchants) {
        if (merchants.isEmpty()) view.openLocationsWhenEmpty();
    }

    public void applySearch(String query) {
        dtlMerchantManager.applySearch(query);
    }

    private void setTabs() {
        view.setTypes(DtlMerchantManager.MERCHANT_TYPES);
    }

    private void preselectProperTab() {
        int index;
        if (dtlMerchantManager.merchantTabSelectionIndexWasSet()) {
            index = dtlMerchantManager.getMerchantTabSelectionIndex();
        } else {
            if (dtlLocationManager.isLocationExternal()) {
                if (((DtlExternalLocation) dtlLocationManager.getSelectedLocation())
                        .getPartnerCount() < 1) index = 1;
                else index = 0;
            } else {
                if (!dtlMerchantManager.offerMerchantsPresent()) index = 1;
                else index = 0;
            }
        }
        //
        trackTabChange(index);
        //
        view.preselectMerchantTabWithIndex(index);
        view.setTabChangeListener();
    }

    public Bundle prepareArgsForTab(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DtlMerchantsListFragment.EXTRA_TYPE, DtlMerchantManager.MERCHANT_TYPES.get(position));
        return bundle;
    }

    /**
     * Analytics-related
     */
    public void trackTabChange(int newPosition) {
        String newTabName = DtlMerchantManager.MERCHANT_TYPES.get(newPosition).equals(DtlMerchantType.OFFER) ?
                TrackingHelper.DTL_ACTION_OFFERS_TAB : TrackingHelper.DTL_ACTION_DINING_TAB;
        trackTab(newTabName);
    }

    private void trackTab(String tabName) {
        dtlMerchantManager.trackTabChange(tabName, dtlLocationManager.getCachedSelectedLocation());
    }

    public void rememberUserTabSelection(int newPosition) {
        dtlMerchantManager.setMerchantTabSelectionIndex(newPosition);
    }

    public void onEvent(final MerchantClickedEvent event) {
        if (!view.isTabletLandscape()) {
            eventBus.cancelEventDelivery(event);
            view.openDetails(event.getMerchantId());
        }
    }

    public interface View extends RxView, ApiErrorView {

        void setTypes(List<DtlMerchantType> types);

        void openDetails(String merchantId);

        void openLocationsWhenEmpty();

        void preselectMerchantTabWithIndex(int tabIndex);

        void updateToolbarTitle(@Nullable DtlLocation dtlLocation);

        /**
         * Do it via a separate method from presenter after we<br />
         * pre-select tab based on vusiness logic - then it won't track pre-selection <br/>
         * as user selection and remember it and mess things up.
         */
        void setTabChangeListener();
    }
}
