package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
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
        if (typePredicate.getMerchantType() == DtlMerchantType.OFFER) view.setComingSoon();
        //
        bindMerchantManager();
    }

    private void bindMerchantManager() {
        bindJobPersistantCached(dtlMerchantManager.getMerchantsExecutor)
                .onSuccess(this::onMerchantsLoaded)
                .onProgress(view::showProgress)
                .onError(throwable -> view.hideProgress());
    }

    private void onMerchantsLoaded(List<DtlMerchant> dtlMerchants) {
        Observable.from(dtlMerchants)
                .compose(new IoToMainComposer<>())
                .filter(typePredicate::apply)
                .toList()
                .subscribe(this::setFilteredMerchants);
    }

    private void setFilteredMerchants(List<DtlMerchant> merchants) {
        view.hideProgress();
        view.setItems(merchants);
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
