package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.PlaceClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

public class DtlPlacesLandscapePresenter extends Presenter<DtlPlacesLandscapePresenter.View> {

    public void onEventMainThread(final PlaceClickedEvent event) {
        view.showDetails(event.getPlace());
    }

    public interface View extends Presenter.View {

        void showDetails(DtlPlace place);
    }
}
