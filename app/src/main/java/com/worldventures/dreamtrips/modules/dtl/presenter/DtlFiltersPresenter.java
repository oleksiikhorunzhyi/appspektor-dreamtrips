package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.CheckFiltersEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlFilterEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

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

        view.setDistanceFilterEnabled(dtlFilterData.isDistanceEnabled());
    }

    public void onEvent(CheckFiltersEvent event) {
        findCurrentLocation(event.getDtlLocation());
    }

    private void findCurrentLocation(DtlLocation selectedLocation) {
        locationDelegate.getLastKnownLocation(location -> locationObtained(location, selectedLocation),
                () -> view.setDistanceFilterEnabled(dtlFilterData.isDistanceEnabled()));
    }

    private void locationObtained(Location location, DtlLocation selectedLocation) {
        if (LocationHelper.checkLocation(DtlFilterData.MAX_DISTANCE,
                new LatLng(location.getLatitude(), location.getLongitude()),
                selectedLocation.getCoordinates().asLatLng())) {
            dtlFilterData.setDistanceEnabled(true);
        } else {
            dtlFilterData.setDistanceEnabled(false);
        }
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
        view.resetFilters();
    }

    public interface View extends Presenter.View {
        void setDistanceFilterEnabled(boolean enabled);

        void resetFilters();
    }
}
