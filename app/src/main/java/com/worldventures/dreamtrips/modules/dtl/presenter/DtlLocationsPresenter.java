package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.GetDtlLocationsQuery;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DtlLocationsPresenter extends Presenter<DtlLocationsPresenter.View> {

    @Inject
    SnappyRepository db;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.startLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
        doRequest(new GetDtlLocationsQuery(5D, 6D, 60),
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
