package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;

import javax.inject.Inject;

public class DtlMerchantCommonDetailsPresenter<T extends DtlMerchantCommonDetailsPresenter.View> extends Presenter<T> {
    protected DtlMerchant merchant;
    protected final String merchantId;

    @Inject
    DtlMerchantRepository dtlMerchantRepository;

    public DtlMerchantCommonDetailsPresenter(String id) {
        this.merchantId = id;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        merchant = dtlMerchantRepository.getMerchantById(merchantId);
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        view.setMerchant(merchant);
    }

    public interface View extends Presenter.View {

        void setMerchant(DtlMerchant merchant);
    }
}
