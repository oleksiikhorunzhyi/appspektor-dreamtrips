package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import java.util.Collections;
import java.util.List;

import techery.io.library.JobSubscriber;

public class DtlMerchantListPresenter extends DtlMerchantsPresenter<DtlMerchantListPresenter.View> {

    public DtlMerchantListPresenter(DtlMerchantType dtlMerchantType) {
        this.dtlMerchantType = dtlMerchantType;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        //
        if (dtlMerchantType == DtlMerchantType.OFFER) view.setComingSoon();
    }

    @Override
    protected JobSubscriber bindApiJob() {
        return super.bindApiJob()
                .onProgress(view::showProgress)
                .onError(throwable -> view.hideProgress());
    }

    @Override
    protected void onMerchantsLoaded() {
        super.onMerchantsLoaded();
        view.hideProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        //
        performFiltering();
    }

    @Override
    protected void afterMapping(List<DtlMerchant> merchants) {
        super.afterMapping(merchants);
        Collections.sort(merchants, DtlMerchant.DISTANCE_COMPARATOR);
    }

    @Override
    protected void merchantsPrepared(List<DtlMerchant> dtlMerchants) {
        view.setItems(dtlMerchants);
    }

    public void onEventMainThread(ToggleMerchantSelectionEvent event) {
        view.toggleSelection(event.getDtlMerchant());
    }

    public interface View extends RxView, ApiErrorView {

        void setItems(List<DtlMerchant> dtlMerchants);

        void showProgress();

        void hideProgress();

        void toggleSelection(DtlMerchant DtlMerchant);

        void setComingSoon();
    }
}
