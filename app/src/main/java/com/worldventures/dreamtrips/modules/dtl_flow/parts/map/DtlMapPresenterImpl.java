package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.event.MapInfoReadyAction;
import com.worldventures.dreamtrips.modules.dtl.event.ShowMapInfoAction;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.FullMerchantParamsHolder;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.RequestSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationFacadeCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;
import com.worldventures.dreamtrips.modules.map.reactive.MapObservableFactory;
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;
import icepick.State;
import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;

@SuppressWarnings("ConstantConditions")
public class DtlMapPresenterImpl extends DtlPresenterImpl<DtlMapScreen, ViewState.EMPTY> implements DtlMapPresenter {

   @Inject LocationDelegate gpsLocationDelegate;
   @Inject SnappyRepository db;
   @Inject MerchantsInteractor merchantInteractor;
   @Inject FilterDataInteractor filterDataInteractor;
   @Inject FullMerchantInteractor fullMerchantInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject PresentationInteractor presentationInteractor;

   @State FullMerchantParamsHolder actionParamsHolder;

   public DtlMapPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      setupToolbarTitlesUpdate();
   }

   @Override
   public void onVisibilityChanged(int visibility) {
      super.onVisibilityChanged(visibility);
      if (visibility == View.VISIBLE) getView().prepareMap();
   }

   private void connectInteractors() {
      locationInteractor.locationSourcePipe()
            .observeSuccess()
            .map(DtlLocationCommand::getResult)
            .map(dtlLocation -> dtlLocation.getCoordinates().asLatLng())
            .filter(command -> getView().isTabletLandscape())
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
            .subscribe(coordinates -> getView().animateTo(coordinates, 0));
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .map(FilterDataAction::getResult)
            .map(FilterData::isDefault)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setFilterButtonState);
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .compose(bindViewIoToMainComposer())
            .map(FilterDataAction::getResult)
            .map(FilterData::isOffersOnly)
            .subscribe(getView()::toggleOffersOnly);
      fullMerchantInteractor.fullMerchantPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .filter(actionState -> !getView().isTabletLandscape())
            .subscribe(new ActionStateSubscriber<FullMerchantAction>()
                  .onSuccess(this::onSuccessMerchantLoad)
                  .onProgress(this::onProgressMerchantLoad)
                  .onFail(this::onFailMerchantLoad));
      presentationInteractor.mapPopupReadyPipe()
            .observeSuccess()
            .compose(bindView())
            .map(MapInfoReadyAction::getResult)
            .subscribe(popupHeight -> getView().prepareInfoWindow(popupHeight));
      merchantInteractor.thinMerchantsHttpPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .filter(action -> action.merchants().isEmpty())
            .subscribe(s -> getView().informUser(R.string.dtl_no_merchants_caption));
      merchantInteractor.thinMerchantsHttpPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<MerchantsAction>()
                  .onStart(this::onStartMerchantsLoad)
                  .onProgress(this::onProgressMerchantsLoad)
                  .onSuccess(this::onMerchantsLoaded)
                  .onFail(this::onFailMerchantsLoad));
   }

   private void onStartMerchantsLoad(MerchantsAction action) {
      if (action.isRefresh()) getView().clearMap();
   }

   private void onProgressMerchantsLoad(MerchantsAction action, Integer progress) {
      getView().showProgress(isNeedShowBlockingProgress(action));
   }

   private void onFailMerchantsLoad(MerchantsAction action, Throwable error) {
      getView().showProgress(false);
      getView().informUser(action.getFallbackErrorMessage());
   }

   private void onMerchantsLoaded(MerchantsAction action) {
      getView().showProgress(false);
      showPins(action.merchants());

      updateMap(action.bundle().location());
   }

   private void onProgressMerchantLoad(CommandWithError<Merchant> action, Integer progress) {
      getView().showBlockingProgress();
   }

   private void onFailMerchantLoad(FullMerchantAction command, Throwable throwable) {
      actionParamsHolder = FullMerchantParamsHolder.fromAction(command);

      getView().hideBlockingProgress();
      getView().showError(command.getErrorMessage());
   }

   private void onSuccessMerchantLoad(FullMerchantAction command) {
      getView().hideBlockingProgress();
      navigateToDetails(command.getResult());
   }

   private boolean isNeedShowBlockingProgress(MerchantsAction action) {
      if (!getView().isTabletLandscape()) return true;
      return action.bundle().requestSource() == RequestSourceType.MAP;
   }

   private void navigateToDetails(Merchant merchant) {
      Path path = new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant, null);
      if (Flow.get(getContext()).getHistory().size() < 2) {
         Flow.get(getContext()).set(path);
      } else {
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
      }
   }

   private void setupToolbarTitlesUpdate() {
      locationInteractor.locationFacadePipe()
            .observeSuccessWithReplay()
            .map(DtlLocationFacadeCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .compose(bindViewIoToMainComposer())
            .map(FilterDataAction::getResult)
            .map(FilterData::searchQuery)
            .subscribe(getView()::updateToolbarSearchCaption);
   }

   private void tryHideMyLocationButton(boolean hide) {
      getView().tryHideMyLocationButton(hide);
   }

   @Override
   public void offersOnlySwitched(boolean isOffersOnly) {
      filterDataInteractor.applyOffersOnly(isOffersOnly);
   }

   @Override
   public void onListClicked() {
      History history = History.single(DtlMerchantsPath.getDefault());
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   @Override
   public void onLoadMoreClicked() {
      merchantInteractor.thinMerchantsHttpPipe()
            .observeWithReplay()
            .take(1)
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<MerchantsAction>()
                  .onSuccess(this::onLoadMoreSuccess)
                  .onProgress(this::onLoadMoreProgress)
                  .onFail(this::onLoadMoreFail));
   }

   private void onLoadMoreSuccess(MerchantsAction action) {
      filterDataInteractor.applyNextPaginatedPageFromMap();
   }

   private void onLoadMoreProgress(MerchantsAction action, Integer progress) {
      getView().showProgress(true);
   }

   private void onLoadMoreFail(MerchantsAction action, Throwable error) {
      getView().showProgress(false);
      filterDataInteractor.applyRetryLoadFromMap();
   }

   @Override
   public void retryLoadMerchant() {
      if (actionParamsHolder == null) return;
      fullMerchantInteractor.load(actionParamsHolder);
   }

   private void showPins(List<ThinMerchant> merchants) {
      getView().showItems(merchants);
   }

   private void updateMap(DtlLocation location) {
      if (location.getLocationSourceType() == LocationSourceType.FROM_MAP && getView().getMap()
            .getCameraPosition().zoom < MapViewUtils.DEFAULT_ZOOM) getView().zoom(MapViewUtils.DEFAULT_ZOOM);

      if (location.getLocationSourceType() != LocationSourceType.NEAR_ME)
         getView().addLocationMarker(location.getCoordinates().asLatLng());
   }

   @Override
   public void onMapLoaded() {
      connectInteractors();

      getFirstCenterLocation().compose(bindViewIoToMainComposer())
            .take(1)
            .subscribe(getView()::centerIn);

      connectButtonsUpdate();

      MapObservableFactory.createMarkerClickObservable(getView().getMap())
            .compose(bindView())
            .subscribe(marker -> getView().markerClick(marker));

      gpsLocationDelegate.getLastKnownLocation()
            .compose(bindViewIoToMainComposer())
            .subscribe(location -> tryHideMyLocationButton(false), throwable -> tryHideMyLocationButton(true));
   }

   private Observable<Location> getFirstCenterLocation() {
      return locationInteractor.locationSourcePipe().observeSuccessWithReplay().map(command -> {
         Location lastPosition = db.getLastMapCameraPosition();
         DtlLocation lastSelectedLocation = command.getResult();
         return lastPosition != null ? lastPosition : (command.isResultDefined() ? lastSelectedLocation.getCoordinates() : new Location(0d, 0d));
      });
   }

   private void connectButtonsUpdate() {
      Observable.combineLatest(
            getMerchantsUpdates(),
            cameraPositionOutOfLimitObservable(),
            updateButtonsFunc
      )
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .distinctUntilChanged()
            .subscribe(pair -> {
               getView().showButtonRedoMerchants(pair.first);
               getView().showLoadMoreButton(pair.second);
            });
   }

   private Observable<Boolean> cameraPositionOutOfLimitObservable() {
      return MapObservableFactory.createCameraChangeObservable(getView().getMap())
            .doOnNext(position -> getView().cameraPositionChange(position))
            .doOnNext(position -> new Location(position.target.latitude, position.target.longitude))
            .flatMap(position -> locationInteractor.locationSourcePipe()
                  .observeSuccessWithReplay()
                  .take(1)
                  .map(DtlLocationCommand::getResult)
                  .map(location -> location.getCoordinates().asLatLng())
                  .map(location -> !DtlLocationHelper.checkMaxDistance(location, position.target)));
   }

   private Observable<ActionState<MerchantsAction>> getMerchantsUpdates() {
      return merchantInteractor.thinMerchantsHttpPipe().observeWithReplay();
   }

   @Override
   public void applySearch(String query) {
      filterDataInteractor.applySearch(query);
   }

   @Override
   public void locationChangeRequested() {
      History history = History.single(new DtlLocationChangePath());
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   @Override
   public void onMarkerClicked(ThinMerchant merchant) {
      getView().showPinInfo(merchant);
      presentationInteractor.toggleSelectionPipe().send(ToggleMerchantSelectionAction.select(merchant));
   }

   @Override
   public void onMarkerPopupDismiss() {
      presentationInteractor.toggleSelectionPipe().send(ToggleMerchantSelectionAction.clear());
   }

   @Override
   public void onMarkerFocused() {
      presentationInteractor.showMapInfoPipe().send(ShowMapInfoAction.create());
   }

   @Override
   public void onLoadMerchantsClick(LatLng latLng) {
      DtlLocation mapSelectedLocation = ImmutableDtlManualLocation.builder()
            .locationSourceType(LocationSourceType.FROM_MAP)
            .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(latLng.latitude, latLng.longitude))
            .build();
      locationInteractor.changeSourceLocation(mapSelectedLocation);
   }

   private static boolean isNeedShowLoadMoreButton(MerchantsAction action) {
      return action.getResult().size() >= FilterData.LIMIT;
   }

   private final Func2<ActionState<MerchantsAction>, Boolean, Pair<Boolean, Boolean>> updateButtonsFunc = (state1, isCenterOutOfLimit) -> {
      switch (state1.status) {
         case SUCCESS: return new Pair<>(isCenterOutOfLimit, !isCenterOutOfLimit && isNeedShowLoadMoreButton(state1.action));
         case FAIL: return new Pair<>(isCenterOutOfLimit, !isCenterOutOfLimit);
         default: return new Pair<>(false, false);
      }
   };
}
