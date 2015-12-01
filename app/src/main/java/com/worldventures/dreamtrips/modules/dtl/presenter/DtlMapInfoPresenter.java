package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.event.TogglePlaceSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;

public class DtlMapInfoPresenter extends DtlPlaceCommonDetailsPresenter<DtlMapInfoPresenter.View> {

    public DtlMapInfoPresenter(DTlMerchant place) {
        super(place);
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
        view.showDetails(place);
    }

    public void onSizeReady(int height) {
        eventBus.post(new DtlMapInfoReadyEvent(height));
    }

    public interface View extends DtlPlaceCommonDetailsPresenter.View {
        void hideLayout();
        void showLayout();
        void showDetails(DTlMerchant place);
    }
}
