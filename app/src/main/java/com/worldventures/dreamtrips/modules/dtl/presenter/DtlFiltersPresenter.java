package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.CheckFiltersEvent;
import com.worldventures.dreamtrips.modules.dtl.event.FilterAttributesSelectAllEvent;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdateFinished;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlacesFilterAttribute;

import java.util.List;

import javax.inject.Inject;

import icepick.State;
import timber.log.Timber;

public class DtlFiltersPresenter extends Presenter<DtlFiltersPresenter.View> {

    @Inject
    LocationDelegate locationDelegate;
    @Inject
    SnappyRepository db;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;

    @State
    DtlFilterData dtlFilterData;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (dtlFilterData == null)
            dtlFilterData = new DtlFilterData();

        dtlFilterDelegate.setDtlFilterData(dtlFilterData);
        attachAmenities();
    }

    public void onEvent(FilterAttributesSelectAllEvent event) {
        toggleAmenitiesSelection(event.isChecked());
    }

    public void onEvent(CheckFiltersEvent event) {
        findCurrentLocation(event.getDtlLocation());
    }

    public void onEvent(PlacesUpdateFinished event) {
        attachAmenities();
    }

    private void attachAmenities() {
        List<DtlPlacesFilterAttribute> amenities = Queryable.from(db.getAmenities()).map(element ->
                        new DtlPlacesFilterAttribute(element.getName())
        ).toList();

        dtlFilterData.setAmenities(amenities);
        view.attachFilterData(dtlFilterData);
    }

    private void findCurrentLocation(DtlLocation selectedLocation) {
        view.bind(locationDelegate.getLastKnownLocation()
                        .compose(new IoToMainComposer<>())
        ).subscribe(location -> locationObtained(location, selectedLocation),
                e -> Timber.e(e, "Smth went wrong"),
                () -> view.attachFilterData(dtlFilterData));
    }

    private void locationObtained(Location location, DtlLocation selectedLocation) {
        if (LocationHelper.checkLocation(DtlFilterData.MAX_DISTANCE,
                new LatLng(location.getLatitude(), location.getLongitude()),
                selectedLocation.getCoordinates().asLatLng(), DtlFilterData.DistanceType.MILES)) {
            dtlFilterData.setDistanceEnabled(true);
        } else {
            dtlFilterData.setDistanceEnabled(false);
        }
    }

    public void priceChanged(int left, int right) {
        dtlFilterData.setPrice(left, right);
    }

    public void distanceChanged(int right) {
        dtlFilterData.setDistanceType(right);
    }

    public void apply() {
        dtlFilterDelegate.performFiltering();
    }

    public void distanceToggle() {
        dtlFilterData.toggleDistance();
        view.attachFilterData(dtlFilterData);
    }

    public void resetAll() {
        dtlFilterData.reset();
        dtlFilterDelegate.performFiltering();
        view.attachFilterData(dtlFilterData);
    }

    private void toggleAmenitiesSelection(boolean selected) {
        dtlFilterData.toggleAmenitiesSelection(selected);
        view.dataSetChanged();
    }

    public interface View extends RxView {

        void attachFilterData(DtlFilterData filterData);

        void dataSetChanged();
    }
}