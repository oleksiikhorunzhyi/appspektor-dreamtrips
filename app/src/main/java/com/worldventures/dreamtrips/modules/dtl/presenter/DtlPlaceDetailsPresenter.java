package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

public class DtlPlaceDetailsPresenter extends Presenter<DtlPlaceDetailsPresenter.View> {

    private final DtlPlace place;

    public DtlPlaceDetailsPresenter(DtlPlace place) {
        this.place = place;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setPlace(place);
    }

    public interface View extends Presenter.View {
        void setPlace(DtlPlace place);
    }
}
