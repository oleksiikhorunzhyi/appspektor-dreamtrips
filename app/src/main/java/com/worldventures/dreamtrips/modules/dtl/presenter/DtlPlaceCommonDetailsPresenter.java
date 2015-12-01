package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;

public class DtlPlaceCommonDetailsPresenter<T extends DtlPlaceCommonDetailsPresenter.View> extends Presenter<T> {
    protected final DTlMerchant place;

    public DtlPlaceCommonDetailsPresenter(DTlMerchant place) {
        this.place = place;
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        view.setPlace(place);
    }

    public interface View extends Presenter.View {
        void setPlace(DTlMerchant place);
    }
}
