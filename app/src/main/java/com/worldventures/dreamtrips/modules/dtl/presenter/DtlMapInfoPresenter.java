package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

public class DtlMapInfoPresenter extends DtlPlaceCommonDetailsPresenter<DtlMapInfoPresenter.View> {

    public DtlMapInfoPresenter(DtlPlace place) {
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
        NavigationBuilder.create()
                .with(activityRouter)
                .data(place)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(Route.DTL_PLACE_DETAILS);
    }

    public void onSizeReady(int height) {
        eventBus.post(new DtlMapInfoReadyEvent(height));
    }

    public interface View extends DtlPlaceCommonDetailsPresenter.View {
        void hideLayout();
        void showLayout();
    }
}
