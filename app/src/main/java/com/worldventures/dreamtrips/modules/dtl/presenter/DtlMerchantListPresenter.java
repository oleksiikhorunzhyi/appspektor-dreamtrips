package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantTypePredicate;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class DtlMerchantListPresenter extends JobPresenter<DtlMerchantListPresenter.View> {

    @Inject
    DtlMerchantManager dtlMerchantManager;
    //
    private final DtlMerchantTypePredicate typePredicate;

    public DtlMerchantListPresenter(DtlMerchantType dtlMerchantType) {
        this.typePredicate = new DtlMerchantTypePredicate(dtlMerchantType);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        //
        bindMerchantManager();
    }

    private void bindMerchantManager() {
        bindJobObservable(dtlMerchantManager.connectMerchantsWithCache())
                .onSuccess(this::onMerchantsLoaded)
                .onProgress(this::showProgress)
                .onError(thr -> hideProgress());
    }

    private void onMerchantsLoaded(List<DtlMerchant> dtlMerchants) {
        Observable.from(dtlMerchants)
                .filter(typePredicate::apply)
                .toList()
                .subscribe(this::setFilteredMerchants);
    }

    private void setFilteredMerchants(List<DtlMerchant> merchants) {
        hideProgress();
        view.setItems(merchants);
    }

    public void onEventMainThread(ToggleMerchantSelectionEvent event) {
        view.toggleSelection(event.getDtlMerchant());
    }

    protected void showProgress() {
        int messageRes = typePredicate.getMerchantType() == DtlMerchantType.OFFER ?
                R.string.dtl_wait_for_offers : R.string.dtl_wait_for_dinings;
        view.showMessage(messageRes);
        view.showProgress();
    }

    protected void hideProgress() {
        int messageRes = typePredicate.getMerchantType() == DtlMerchantType.OFFER ?
                R.string.dtl_coming_soon_offers : R.string.dtl_place_list_empty_text;
        view.showMessage(messageRes);
        view.hideProgress();
    }

    public interface View extends RxView, ApiErrorView {

        void setItems(List<DtlMerchant> dtlMerchants);

        void showProgress();

        void hideProgress();

        void showMessage(int textResourceId);

        void toggleSelection(DtlMerchant DtlMerchant);

    }
}
