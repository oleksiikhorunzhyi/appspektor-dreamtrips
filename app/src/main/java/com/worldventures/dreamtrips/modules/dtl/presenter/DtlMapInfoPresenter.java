package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.event.TogglePlaceSelectionEvent;

public class DtlMapInfoPresenter extends DtlPlaceCommonDetailsPresenter<DtlMapInfoPresenter.View> {

    public DtlMapInfoPresenter(String id) {
        super(id);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.hideLayout();
    }

    public void onEvent(DtlShowMapInfoEvent event) {
        view.showLayout();
    }

    public void onPlaceClick() {
        eventBus.post(new TogglePlaceSelectionEvent(place));
        view.showDetails(place.getId());
    }

    public void onSizeReady(int height) {
        eventBus.post(new DtlMapInfoReadyEvent(height));
    }

    public interface View extends DtlPlaceCommonDetailsPresenter.View {
        void hideLayout();
        void showLayout();
        void showDetails(String id);
    }
}
