package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.techery.spares.module.Injector;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePresenterImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MasterToolbarPresenterImpl
        extends DtlPresenterImpl<MasterToolbarScreen, MasterToolbarState>
        implements MasterToolbarPresenter {

    @Inject
    DtlFilterMerchantInteractor filterInteractor;
    @Inject
    DtlLocationInteractor locationInteractor;
    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    DtlMerchantInteractor merchantInteractor;
    //
    @State
    DtlLocationChangePresenterImpl.ScreenMode screenMode = DtlLocationChangePresenterImpl.ScreenMode.NEARBY_LOCATIONS;
    @State
    ArrayList<DtlExternalLocation> dtlNearbyLocations = new ArrayList<>();
    //
    private Subscription locationRequestNoFallback;
    //
    private AtomicBoolean showAutodetectButton = new AtomicBoolean(Boolean.FALSE);
    private AtomicBoolean noMerchants = new AtomicBoolean(Boolean.FALSE);


    public MasterToolbarPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void onNewViewState() {
        state = new MasterToolbarState();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        state.setPopupShowing(getView().isSearchPopupShowing());
        state.setDtlNearbyLocations(dtlNearbyLocations);
        state.setScreenMode(screenMode);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle instanceState) {
        super.onRestoreInstanceState(instanceState);
        if (state.isPopupShowing()) getView().toggleSearchPopupVisibility(true);
        this.dtlNearbyLocations = state.getDtlNearbyLocations();
        this.screenMode = state.getScreenMode();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        apiErrorPresenter.setView(getView());
        //
        bindToolbarLocationCaptionUpdates();
        connectFilterDataChanges();
        //
        tryHideNearMeButton();
        // remember this observable - we will start listening to search below only after this fires
        updateToolbarTitles();
        //
        connectNearbyLocations();
        connectLocationsSearch();
        connectMerchants();
        connectLocationDelegateNoFallback();
        connectToolbarLocationSearchInput();
        //
        connectFilterToogle();
    }

    private void connectFilterToogle() {
        filterInteractor.filterDataPipe().observeSuccessWithReplay()
                .first()
                .map(DtlFilterDataAction::getResult)
                .map(DtlFilterData::isOffersOnly)
                .subscribe(getView()::toggleDiningFilterSwitch);
    }

    private void connectFilterDataChanges() {
        filterInteractor.filterDataPipe().observeSuccess()
                .map(DtlFilterDataAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData -> {
                    getView().setFilterButtonState(!dtlFilterData.isDefault());
                });
    }

    private void connectMerchants() {
        merchantInteractor.merchantsActionPipe()
                .createObservableResult(DtlMerchantsAction.restore())
                .compose(bindViewIoToMainComposer())
                .map(DtlMerchantsAction::getResult)
                .map(List::isEmpty)
                .subscribe(noMerchants::set);
    }

    @Override
    public void applyOffersOnlyFilterState(boolean enabled) {
        filterInteractor.filterDataPipe()
                .send(DtlFilterDataAction.applyOffersOnly(enabled));
    }

    @Override
    public void applySearch(String query) {
        filterInteractor.filterDataPipe().send(DtlFilterDataAction.applySearch(query));
    }

    private void connectToolbarLocationSearchInput() {
        getView().provideLocationSearchObservable()
                .filter(dtlLocationCommand -> getView().isSearchPopupShowing())
                .debounce(250L, TimeUnit.MILLISECONDS)
                .compose(bindView())
                .subscribe(this::locationSearch);
    }

    private void updateToolbarTitles() {
        locationInteractor.locationPipe().observeSuccessWithReplay()
                .first()
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(getView()::updateToolbarLocationTitle);
        filterInteractor.filterDataPipe().observeSuccessWithReplay()
                .first()
                .compose(bindViewIoToMainComposer())
                .map(DtlFilterDataAction::getResult)
                .map(DtlFilterData::getSearchQuery)
                .subscribe(getView()::updateToolbarSearchCaption);
    }

    private void bindToolbarLocationCaptionUpdates() {
        locationInteractor.locationPipe().observeSuccess()
                .filter(dtlLocationCommand -> !getView().isSearchPopupShowing())
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(getView()::updateToolbarLocationTitle);
    }

    private void connectLocationDelegateNoFallback() {
        locationRequestNoFallback = gpsLocationDelegate.requestLocationUpdate()
                .compose(bindViewIoToMainComposer())
                .timeout(10L, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::onLocationObtained, throwable -> {
                    if (throwable instanceof TimeoutException) getView().hideProgress();
                });
    }

    private void onLocationObtained(Location location) {
        switch (screenMode) {
            case NEARBY_LOCATIONS:
                locationInteractor.nearbyLocationPipe().send(new DtlNearbyLocationAction(location));
                break;
            case AUTO_NEAR_ME:
                DtlLocation dtlLocation = ImmutableDtlManualLocation.builder()
                        .locationSourceType(LocationSourceType.NEAR_ME)
                        .longName(context.getString(R.string.dtl_near_me_caption))
                        .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(location))
                        .build();
                locationInteractor.locationPipe().send(DtlLocationCommand.change(dtlLocation));
                filterInteractor.filterMerchantsActionPipe().clearReplays();
                merchantInteractor.merchantsActionPipe().send(DtlMerchantsAction.load(location));
                break;
        }
    }

    @Override
    public void loadNearMeRequested() {
        screenMode = DtlLocationChangePresenterImpl.ScreenMode.AUTO_NEAR_ME;
        //
        if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed())
            locationRequestNoFallback.unsubscribe();
        //
        gpsLocationDelegate.requestLocationUpdate()
                .compose(bindViewIoToMainComposer())
                .doOnSubscribe(getView()::showProgress)
                .subscribe(this::onLocationObtained, this::onLocationError);
    }

    private void tryHideNearMeButton() {
        locationInteractor.locationPipe().observeSuccess()
                .compose(bindViewIoToMainComposer())
                .distinctUntilChanged(Command::getResult)
                .map(Command::getResult)
                .map(DtlLocation::getLocationSourceType)
                .map(mode -> mode != LocationSourceType.NEAR_ME)
                .subscribe(showAutodetectButton::set);
    }

    private void connectLocationsSearch() {
        locationInteractor.searchLocationPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlSearchLocationAction>()
                        .onStart(command -> getView().showProgress())
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(this::onSearchFinished));
    }

    private void onSearchFinished(DtlSearchLocationAction action) {
        getView().setItems(action.getResult(), false);
    }

    private void locationSearch(String query) {
        screenMode = DtlLocationChangePresenterImpl.ScreenMode.SEARCH;
        locationInteractor.searchLocationPipe().cancelLatest();
        locationInteractor.searchLocationPipe().send(new DtlSearchLocationAction(query.trim()));
    }

    @Override
    public void onLocationResolutionGranted() {
        if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed())
            locationRequestNoFallback.unsubscribe();
        //
        gpsLocationDelegate.requestLocationUpdate()
                .compose(new IoToMainComposer<>())
                .subscribe(this::onLocationObtained, this::onLocationError);
    }

    @Override
    public void onLocationResolutionDenied() {
        getView().hideProgress();
    }

    @Override
    public void onShowToolbar() {
        if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed())
            locationRequestNoFallback.unsubscribe();
        //
        gpsLocationDelegate.requestLocationUpdate()
                .compose(new IoToMainComposer<>())
                .map(DtlNearbyLocationAction::new)
                .subscribe(locationInteractor.nearbyLocationPipe()::send, this::onLocationError);
    }

    @Override
    public boolean needShowAutodetectButton() {
        return showAutodetectButton.get();
    }

    private void onLocationError(Throwable e) {
        if (e instanceof LocationDelegate.LocationException)
            getView().locationResolutionRequired(
                    ((LocationDelegate.LocationException) e).getStatus());
        else onLocationResolutionDenied();
    }

    private void connectNearbyLocations() {
        locationInteractor.nearbyLocationPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlNearbyLocationAction>()
                        .onStart(command -> getView().showProgress())
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(this::onLocationsLoaded));
    }

    private void onLocationsLoaded(DtlNearbyLocationAction action) {
        getView().hideProgress();
        showLoadedLocations(action.getResult());
    }

    private void showLoadedLocations(List<DtlExternalLocation> locations) {
        dtlNearbyLocations.clear();
        dtlNearbyLocations.addAll(locations);
        getView().setItems(locations, !locations.isEmpty());
    }

    @Override
    public void locationSelected(DtlExternalLocation dtlExternalLocation) {
//        trackLocationSelection(location); // TODO :: 4/20/16 new analytics
        locationInteractor.searchLocationPipe().clearReplays();
        locationInteractor.locationPipe().send(DtlLocationCommand.change(dtlExternalLocation));
        filterInteractor.filterMerchantsActionPipe().clearReplays();
        merchantInteractor.merchantsActionPipe().send(DtlMerchantsAction.load(dtlExternalLocation.getCoordinates().asAndroidLocation()));
    }
}