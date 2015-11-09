package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.DtlFilterEvent;
import com.worldventures.dreamtrips.modules.dtl.event.LocationObtainedEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DtlFilterData;

import javax.inject.Inject;

import icepick.State;

public class DtlFiltersPresenter extends Presenter<DtlFiltersPresenter.View> {

    @Inject
    LocationDelegate locationDelegate;

    @State
    DtlFilterData dtlFilterData;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (dtlFilterData == null) {
            dtlFilterData = new DtlFilterData();
        }

        findCurrentLocation();
    }

    private void findCurrentLocation() {
        locationDelegate.getLastKnownLocation(location -> dtlFilterData.setDistanceEnabled(true),
                () -> view.setDistanceFilterEnabled(dtlFilterData.isDistanceEnabled()));
    }

    public void onEvent(LocationObtainedEvent event) {
        if (event.getLocation() == null) dtlFilterData.setDistanceEnabled(false);
        else dtlFilterData.setDistanceEnabled(true);
    }

    public void priceChanged(int left, int right) {
        dtlFilterData.setPrice(left, right);
    }

    public void distanceChanged(int right) {
        dtlFilterData.setDistance(right);
    }

    public void apply() {
        eventBus.post(new DtlFilterEvent(dtlFilterData));
    }

    public void resetAll() {
        dtlFilterData.reset();
        eventBus.post(new DtlFilterEvent(dtlFilterData));

    }

    public interface View extends Presenter.View {
        void setDistanceFilterEnabled(boolean enabled);
    }
}
