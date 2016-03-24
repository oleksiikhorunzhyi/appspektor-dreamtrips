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
        preselectProperTab();
        //
        bindJobObservable(dtlMerchantManager.connectMerchantsWithCache())
                .onError(apiErrorPresenter::handleError);
        //
        view.bind(dtlLocationManager.getLocationStream()).compose(new IoToMainComposer<>())
                .subscribe(view::updateToolbarTitle);
    }

    public void applySearch(String query) {
        dtlMerchantManager.applySearch(query);
    }

    private void setTabs() {
        view.setTypes(DtlMerchantManager.MERCHANT_TYPES);
        view.updateSelection();
    }

    private void preselectProperTab() {
        if (dtlMerchantManager.merchantTabSelectionIndexWasSet()) {
            view.preselectMerchantTabWithIndex(dtlMerchantManager.getMerchantTabSelectionIndex());
        } else {
            if (dtlLocationManager.isLocationExternal()) {
                if (((DtlExternalLocation) dtlLocationManager.getSelectedLocation()).getPartnerCount() < 1) {
                    view.preselectMerchantTabWithIndex(1);
                    return;
                }
            } else {
                if (!dtlMerchantManager.offerMerchantsPresent())
                    view.preselectMerchantTabWithIndex(1);
            }
        }
        //
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
        TrackingHelper.dtlMerchantsTab(tabName, dtlLocationManager.getCachedSelectedLocation());
    }

    public void rememberUserTabSelection(int newPosition) {
        dtlMerchantManager.setMerchantTabSelectionIndex(newPosition);
    }

    public void onEventMainThread(final MerchantClickedEvent event) {
        if (!view.isTabletLandscape()) {
            view.openDetails(event.getMerchantId());
        }
    }

    public interface View extends RxView, ApiErrorView {

        void setTypes(List<DtlMerchantType> types);

        void updateSelection();

        void openDetails(String merchantId);

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
