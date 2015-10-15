package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.DtlModule;
import com.worldventures.dreamtrips.modules.dtl.api.GetDtlLocationsQuery;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import java.util.List;

import javax.inject.Inject;

public class DtlLocationsPresenter extends Presenter<DtlLocationsPresenter.View> {

    @Inject
    SnappyRepository db;

    @Inject
    LocationDelegate locationDelegate;

    private List<DtlLocation> dtlLocations;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.startLoading();
    }

    public void permissionGranted() {
        double latitude;
        double longitude;
        Location currentLocation = locationDelegate.getLastKnownLocation();
        if (currentLocation != null) {
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
        } else {
            // use stub data for location request
            latitude = DtlModule.LAT;
            longitude = DtlModule.LNG;
        }
        doRequest(new GetDtlLocationsQuery(latitude, longitude, 50),
                dtlLocations -> {
                    this.dtlLocations = dtlLocations;
                    view.setItems(dtlLocations);
                    view.finishLoading();
                },
                spiceException -> {
                    this.handleError(spiceException);
                    view.finishLoading();
                });
    }

    public void search(String caption) {
        view.setItems(Queryable.from(dtlLocations).filter(dtlLocation ->
                dtlLocation.getName().toLowerCase().contains(caption) ||
                        dtlLocation.getCountryName().toLowerCase().contains(caption)).toList());
    }

    public void flushSearch() {
        view.setItems(dtlLocations);
    }


    public void onLocationClicked(DtlLocation location) {
        db.saveSelectedDtlLocation(location);
        view.openLocation(new PlacesBundle(location));
    }

    public interface View extends Presenter.View {

        void setItems(List<DtlLocation> dtlLocations);

        void startLoading();

        void finishLoading();

        void openLocation(PlacesBundle bundle);
    }
}
