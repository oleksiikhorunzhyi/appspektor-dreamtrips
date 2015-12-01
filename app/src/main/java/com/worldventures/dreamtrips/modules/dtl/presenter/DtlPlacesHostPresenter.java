package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.PlaceClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;

public class DtlPlacesHostPresenter extends Presenter<DtlPlacesHostPresenter.View> {

    public void onEventMainThread(final PlaceClickedEvent event) {
        if (view.isTabletLandscape()) view.showDetails(event.getPlace());
    }

    public interface View extends Presenter.View {

        void showDetails(DTlMerchant place);
    }
}
