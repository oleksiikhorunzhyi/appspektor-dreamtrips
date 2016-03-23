package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.api.error.DtApiException;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import java.util.List;

import javax.inject.Inject;

public class DtlLocationsSearchPresenter extends JobPresenter<DtlLocationsSearchPresenter.View> {

    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    DtlMerchantManager dtlMerchantManager;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        connectLocationsSearchExecutor();
        view.showDefaultCaption(true);
    }

    private void connectLocationsSearchExecutor() {
        bindJobCached(dtlLocationManager.searchLocationExecutor)
                .onProgress(view::showProgress)
                .onError(this::onSearchError)
                .onSuccess(this::onSearchFinished);
    }

    public void onLocationSelected(DtlExternalLocation location) {
        trackLocationSelection(location);
        dtlLocationManager.persistLocation(location);
        dtlMerchantManager.clean();
        view.navigateToMerchants();
    }

    public void searchClosed() {
        dtlLocationManager.searchLocations("");
        view.navigateToNearby();
    }

    public void search(String query) {
        view.showDefaultCaption(query.isEmpty());
        dtlLocationManager.searchLocations(query);
    }

    public void onSearchFinished(List<DtlExternalLocation> locations) {
        view.hideProgress();
        view.setItems(locations);
    }

    public void onSearchError(Throwable e) { // TODO :: 3/16/16 TEMPORARY NOT TO BULK USER WITH ERRORS
        // TODO :: 3/16/16 RELATED TO DtlLocationManager bug:
        // when we perform local search. e.g. we enter 4th symbol right after 3rd, when API-call is
        // still going - we get "Smth went wrong error" and then it presents loading results as expected
        if (e instanceof DtApiException) apiErrorPresenter.handleError(e);
    }

    /**
     * Analytic-related
     */
    private void trackLocationSelection(DtlExternalLocation newLocation) {
        TrackingHelper.searchLocation(newLocation);
    }

    public interface View extends RxView, ApiErrorView {

        void setItems(List<DtlExternalLocation> dtlExternalLocations);

        void showProgress();

        void hideProgress();

        void navigateToNearby();

        void navigateToMerchants();

        void showDefaultCaption(boolean visible);
    }
}
