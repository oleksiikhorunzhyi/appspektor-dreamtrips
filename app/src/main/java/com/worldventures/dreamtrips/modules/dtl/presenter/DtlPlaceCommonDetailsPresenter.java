package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import javax.inject.Inject;

public class DtlPlaceCommonDetailsPresenter<T extends DtlPlaceCommonDetailsPresenter.View> extends Presenter<T> {
    protected DtlMerchant place;
    protected final String merchantId;

    @Inject
    DtlMerchantRepository dtlMerchantRepository;

    public DtlPlaceCommonDetailsPresenter(String id) {
        this.merchantId = id;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        place = dtlMerchantRepository.getMerchantById(merchantId);
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        view.setPlace(place);
    }

    public interface View extends Presenter.View {
        void setPlace(DtlMerchant place);
    }
}
