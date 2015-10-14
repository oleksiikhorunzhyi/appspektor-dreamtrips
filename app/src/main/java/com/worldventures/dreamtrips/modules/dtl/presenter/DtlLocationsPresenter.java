package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
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

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.startLoading();
    }

    public void permissionGranted() {
        Location currentLocation = locationDelegate.getLastKnownLocation();
        doRequest(new GetDtlLocationsQuery(currentLocation.getLatitude(),
                        currentLocation.getLongitude(), 50),
                dtlLocations -> {
                    view.setItems(dtlLocations);
                    view.finishLoading();
                },
                spiceException -> {
                    this.handleError(spiceException);
                    view.finishLoading();
                });
    }

    public void onLocationClicked(DtlLocation location) {
        db.saveSelectedDtlLocation(location);
        view.openLocation(new PlacesBundle(location));
    }

    public interface View extends Presenter.View {

        void setItems(List<DtlLocation> dtlLocations);

        void startLoading();

        void finishLoading();

        void showSearch();

        void openLocation(PlacesBundle bundle);
    }
}
