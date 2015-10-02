package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.GetDtlLocationsQuery;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import java.util.List;

import javax.inject.Inject;

public class LocationsPresenter extends Presenter<LocationsPresenter.View> {

    @Inject
    SnappyRepository db;

    List<DtlLocation> locationsList;

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
                    view.getAdapter().setItems(dtlLocations);
                    view.finishLoading();
                },
                spiceException -> {
                    this.handleError(spiceException);
                    view.finishLoading();
                });
    }

    public interface View extends Presenter.View {

        BaseArrayListAdapter<DtlLocation> getAdapter();

        void startLoading();

        void finishLoading();

        void showSearch();

        void openLocation(PlacesBundle bundle);
    }
}
