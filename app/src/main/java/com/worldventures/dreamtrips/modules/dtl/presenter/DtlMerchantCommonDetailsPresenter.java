package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class DtlMerchantCommonDetailsPresenter<T extends DtlMerchantCommonDetailsPresenter.View> extends Presenter<T> {
    protected final DtlMerchant merchant;

    public DtlMerchantCommonDetailsPresenter(DtlMerchant merchant) {
        this.merchant = merchant;
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
