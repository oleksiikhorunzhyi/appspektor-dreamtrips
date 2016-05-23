package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import android.content.Context;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationService;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlLocationsSearchPresenterImpl extends DtlPresenterImpl<DtlLocationsSearchScreen, DtlLocationsSearchViewState>
        implements DtlLocationsSearchPresenter {

    @Inject
    DtlFilterMerchantService filterService;
    @Inject
    DtlLocationService locationService;

    public DtlLocationsSearchPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getView().toggleDefaultCaptionVisibility(true);
        //
        connectLocationsSearch();
        apiErrorPresenter.setView(getView());
    }

    private void connectLocationsSearch() {
        locationService.searchLocationPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlSearchLocationAction>()
                        .onStart(command -> getView().showProgress())
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(this::onSearchFinished));
    }

    private void onSearchFinished(DtlSearchLocationAction action) {
        List<DtlExternalLocation> locations = action.getResult();
        getView().hideProgress();
        getView().setItems(locations);
        if (TextUtils.isEmpty(action.getQuery()) && !locations.isEmpty())
            getView().toggleDefaultCaptionVisibility(false);
    }

    @Override
    public void searchClosed() {
        sendSearchAction("");
        Flow.get(getContext()).goBack();
    }

    @Override
    public void search(String query) {
        getView().toggleDefaultCaptionVisibility(query.isEmpty());
        sendSearchAction(query);
    }

    private void sendSearchAction(String query) {
        locationService.searchLocationPipe().cancelLatest();
        locationService.searchLocationPipe().send(new DtlSearchLocationAction(query.trim()));
    }

    @Override
    public void onLocationSelected(DtlExternalLocation location) {
        trackLocationSelection(location);
        locationService.locationPipe().send(DtlLocationCommand.change(location));
        filterService.filterMerchantsActionPipe().clearReplays();
        History history = History.single(new DtlMerchantsPath());
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
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
