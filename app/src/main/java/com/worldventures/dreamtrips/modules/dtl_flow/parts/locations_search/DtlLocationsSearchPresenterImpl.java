package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import android.content.Context;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.DtApiException;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import techery.io.library.JobSubscriber;

public class DtlLocationsSearchPresenterImpl extends DtlPresenterImpl<DtlLocationsSearchScreen, DtlLocationsSearchViewState>
        implements DtlLocationsSearchPresenter {

    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    DtlMerchantManager dtlMerchantManager;

    public DtlLocationsSearchPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
        apiErrorPresenter.setView(getView());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getView().toggleDefaultCaptionVisibility(true);
        //
        connectLocationsSearchExecutor();
    }

    private void connectLocationsSearchExecutor() {
        dtlLocationManager.searchLocationExecutor.connectWithCache()
                .compose(bindViewIoToMainComposer())
                .subscribe(new JobSubscriber<List<DtlExternalLocation>>()
                        .onProgress(getView()::showProgress)
                        .onError(this::onSearchError)
                        .onSuccess(this::onSearchFinished));
    }

    private void onSearchFinished(List<DtlExternalLocation> locations) {
        getView().hideProgress();
        getView().setItems(locations);
        if (TextUtils.isEmpty(dtlLocationManager.getQuery()) && !locations.isEmpty())
            getView().toggleDefaultCaptionVisibility(false);
    }

    @Override
    public void searchClosed() {
        dtlLocationManager.searchLocations("");
        Flow.get(getContext()).goBack();
    }

    @Override
    public void search(String query) {
        getView().toggleDefaultCaptionVisibility(query.isEmpty());
        dtlLocationManager.searchLocations(query);
    }

    @Override
    public void onLocationSelected(DtlExternalLocation location) {
        trackLocationSelection(location);
        dtlLocationManager.persistLocation(location);
        dtlMerchantManager.clean();
        History history = History.single(new DtlMerchantsPath());
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    public void onSearchError(Throwable e) {
        // TODO :: 3/16/16 TEMPORARY NOT TO BULK USER WITH ERRORS
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

    @Override
    public int getToolbarMenuRes() {
        return R.menu.menu_locations_search;
    }

    @Override
    public void onToolbarMenuPrepared(Menu menu) {
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        return false;
    }
}
