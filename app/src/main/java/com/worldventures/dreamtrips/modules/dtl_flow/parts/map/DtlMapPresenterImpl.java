package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.modules.dtl.event.MapInfoReadyAction;
import com.worldventures.dreamtrips.modules.dtl.event.ShowMapInfoAction;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.FullMerchantParamsHolder;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.RequestSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationFacadeCommand;
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

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;
import icepick.State;
import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;

@SuppressWarnings("ConstantConditions")
public class DtlMapPresenterImpl extends DtlPresenterImpl<DtlMapScreen, ViewState.EMPTY> implements DtlMapPresenter {

   @Inject LocationDelegate gpsLocationDelegate;
   @Inject MerchantsInteractor merchantInteractor;
   @Inject FilterDataInteractor filterDataInteractor;
   @Inject FullMerchantInteractor fullMerchantInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject PresentationInteractor presentationInteractor;
   @Inject AttributesInteractor attributesInteractor;

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
            .map(LocationCommand::getResult)
            .map(DtlLocation::coordinates)
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
            .map(FilterData::getMerchantType)
            .subscribe(getView()::updateMerchantType);
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
            .subscribe(pair -> getView().prepareInfoWindow(pair.first, pair.second));
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
      updateMapZoom(action.merchants());
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
            .map(LocationFacadeCommand::getResult)
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
   public void onLoadMerchantsType(List<String> merchantType) {
      filterDataInteractor.applyMerchantTypes(merchantType);
   }

   @Override
   public void loadAmenities(List<String> merchantType) {
      attributesInteractor.requestAmenities(merchantType);
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

   private void updateMapZoom(List<ThinMerchant> merchants) {
      if (merchants.isEmpty()) return;
      getView().zoomBounds(buildBounds(merchants));
   }

   private LatLngBounds buildBounds(List<ThinMerchant> merchants) {
      final LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
      for (ThinMerchant merchant : merchants) {
         boundsBuilder.include(new LatLng(merchant.coordinates().lat(), merchant.coordinates().lng()));
      }
      return boundsBuilder.build();
   }

   private void showPins(List<ThinMerchant> merchants) {
      getView().showItems(merchants);
   }

   private void updateMap(DtlLocation location) {
      if (location.locationSourceType() == LocationSourceType.FROM_MAP && getView().getMap()
            .getCameraPosition().zoom < MapViewUtils.DEFAULT_ZOOM) getView().zoom(MapViewUtils.DEFAULT_ZOOM);

      if (location.locationSourceType() != LocationSourceType.NEAR_ME)
         getView().addLocationMarker(location.coordinates());
   }

   @Override
   public void onMapLoaded() {
      getFirstCenterLocation().subscribe(getView()::centerIn);
      connectInteractors();
      connectButtonsUpdate();

      MapObservableFactory.createMarkerClickObservable(getView().getMap())
            .compose(bindView())
            .subscribe(marker -> getView().markerClick(marker));

      gpsLocationDelegate.getLastKnownLocation()
            .compose(bindViewIoToMainComposer())
            .subscribe(location -> tryHideMyLocationButton(false), throwable -> tryHideMyLocationButton(true));
   }

   private Observable<LatLng> getFirstCenterLocation() {
      return locationInteractor.locationSourcePipe().observeSuccessWithReplay()
            .filter(LocationCommand::isResultDefined)
            .map(Command::getResult)
            .map(DtlLocation::coordinates)
            .take(1)
            .compose(bindViewIoToMainComposer());
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
            .compose(bindView())
            .flatMap(position -> locationInteractor.locationSourcePipe()
                  .observeSuccessWithReplay()
                  .take(1)
                  .compose(bindView())
                  .map(LocationCommand::getResult)
                  .map(location -> location.isOutOfMaxDistance(position.target)));
   }

   private Observable<ActionState<MerchantsAction>> getMerchantsUpdates() {
      return merchantInteractor.thinMerchantsHttpPipe().observeWithReplay().compose(bindView());
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
      DtlLocation mapSelectedLocation = ImmutableDtlLocation.builder()
            .isExternal(false)
            .locationSourceType(LocationSourceType.FROM_MAP)
            .coordinates(latLng)
            .build();
      locationInteractor.changeSourceLocation(mapSelectedLocation);
   }

   private static boolean isNeedShowLoadMoreButton(MerchantsAction action) {
      return action.getResult().size() >= FilterData.LIMIT;
   }

   private final Func2<ActionState<MerchantsAction>, Boolean, Pair<Boolean, Boolean>> updateButtonsFunc = (state, isCenterOutOfLimit) -> {
      switch (state.status) {
         case SUCCESS: return new Pair<>(isCenterOutOfLimit, !isCenterOutOfLimit && isNeedShowLoadMoreButton(state.action));
         case FAIL: return new Pair<>(isCenterOutOfLimit, !isCenterOutOfLimit);
         default: return new Pair<>(false, false);
      }
   };
}
