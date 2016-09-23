package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.MapInfoReadyAction;
import com.worldventures.dreamtrips.modules.dtl.event.ShowMapInfoAction;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.MerchantByIdParamsHolder;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantByIdCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.ThinMerchantsCommand;
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
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;

import static rx.Observable.just;

@SuppressWarnings("ConstantConditions")
public class DtlMapPresenterImpl extends DtlPresenterImpl<DtlMapScreen, ViewState.EMPTY> implements DtlMapPresenter {

   public static final int MAX_DISTANCE = 50;

   @Inject LocationDelegate gpsLocationDelegate;
   @Inject SnappyRepository db;
   @Inject Presenter.TabletAnalytic tabletAnalytic;
   @Inject Janet janet;
   @Inject DtlMerchantInteractor merchantInteractor;
   @Inject DtlFilterMerchantInteractor filterInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject DtlTransactionInteractor pipesInteractor;
   @Inject PresentationInteractor presentationInteractor;
   //
   @State MerchantByIdParamsHolder actionParamsHolder;

   public DtlMapPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      //
      filterInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlFilterDataAction::getResult)
            .map(DtlFilterData::isOffersOnly)
            .subscribe(getView()::toggleDiningFilterSwitch);
      getView().getToggleObservable().subscribe(offersOnly -> filterInteractor.filterDataPipe()
            .send(DtlFilterDataAction.applyOffersOnly(offersOnly)));
      //
      updateToolbarTitles();
      bindToolbarTitleUpdates();
      updateFilterButtonState();
   }

   @Override
   public void onVisibilityChanged(int visibility) {
      super.onVisibilityChanged(visibility);
      if (visibility == View.VISIBLE) getView().prepareMap();
   }

   protected void connectInteractors() {
      // TODO :: temporary solutions
      locationInteractor.locationPipe()
            .observeSuccess()
            .map(DtlLocationCommand::getResult)
            .filter(command -> tabletAnalytic.isTabletLandscape())
            .distinctUntilChanged(loc -> loc.getCoordinates().asLatLng())
            .map(DtlLocation::getCoordinates)
            .compose(bindViewIoToMainComposer())
            .subscribe(loc -> getView().animateTo(loc.asLatLng(), 0));
      merchantInteractor.thinMerchantsHttpPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionStateToActionTransformer<>())
            .filter(action -> action.getResult().isEmpty())
            .subscribe(s -> getView().informUser(R.string.dtl_no_merchants_caption), throwable -> {});
      merchantInteractor.thinMerchantsHttpPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ThinMerchantsCommand>()
                  .onStart(action -> getView().showProgress(true))
                  .onSuccess(action -> onMerchantsLoaded(action.getResult()))
                  .onFail((action, throwable) -> {
                     getView().showProgress(false);
                     apiErrorPresenter.handleActionError(action, throwable);}));
      filterInteractor.filterDataPipe()
            .observeSuccess()
            .map(DtlFilterDataAction::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(dtlFilterData -> getView().setFilterButtonState(!dtlFilterData.isDefault()));
      //
      merchantInteractor.merchantByIdHttpPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<MerchantByIdCommand>()
                  .onSuccess(this::onSuccessMerchantLoad)
                  .onProgress(this::onProgressMerchantLoad)
                  .onFail(this::onFailMerchantLoad));
      //
      presentationInteractor.mapPopupReadyPipe()
            .observeSuccess()
            .compose(bindView())
            .map(MapInfoReadyAction::getResult)
            .subscribe(popupHeight -> getView().prepareInfoWindow(popupHeight));
   }

   @SuppressWarnings("unused")
   protected void onProgressMerchantLoad(CommandWithError<Merchant> action, Integer progress) {
      getView().showBlockingProgress();
   }

   @SuppressWarnings("unused")
   protected void onFailMerchantLoad(MerchantByIdCommand command, Throwable throwable) {
      actionParamsHolder = MerchantByIdParamsHolder.fromAction(command);
      //
      getView().hideBlockingProgress();
      getView().showError(command.getErrorMessage());
   }

   @SuppressWarnings("unused")
   protected void onSuccessMerchantLoad(MerchantByIdCommand command) {
      merchantInteractor.merchantByIdHttpPipe().clearReplays();
      //
      getView().hideBlockingProgress();
      navigateToDetails(command.getResult());
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

   private void updateToolbarTitles() {
      locationInteractor.locationPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlLocationCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
      filterInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .compose(bindViewIoToMainComposer())
            .map(DtlFilterDataAction::getResult)
            .map(DtlFilterData::getSearchQuery)
            .subscribe(getView()::updateToolbarSearchCaption);
   }

   private void bindToolbarTitleUpdates() {
      locationInteractor.locationPipe()
            .observeSuccess()
            .map(DtlLocationCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
   }

   private void updateFilterButtonState() {
      filterInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlFilterDataAction::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(dtlFilterData -> getView().setFilterButtonState(!dtlFilterData.isDefault()));
   }

   protected void tryHideMyLocationButton(boolean hide) {
      getView().tryHideMyLocationButton(hide);
   }

   protected Observable<Location> getFirstCenterLocation() {
      return locationInteractor.locationPipe().observeSuccessWithReplay().map(command -> {
         Location lastPosition = db.getLastMapCameraPosition();
         boolean validLastPosition = lastPosition != null && lastPosition.getLat() != 0 && lastPosition.getLng() != 0;
         DtlLocation lastSelectedLocation = command.getResult();
         return validLastPosition ? lastPosition : (command.isResultDefined() ? lastSelectedLocation.getCoordinates() : new Location(0d, 0d));
      });
   }

   protected Observable<Boolean> showingLoadMerchantsButton() {
      return MapObservableFactory.createCameraChangeObservable(getView().getMap())
            .doOnNext(position -> getView().cameraPositionChange(position))
            .doOnNext(position -> db.saveLastMapCameraPosition(new Location(position.target.latitude, position.target.longitude)))
            .flatMap(position -> {
               if (position.zoom < MapViewUtils.DEFAULT_ZOOM) {
                  return just(true);
               }
               return locationInteractor.locationPipe()
                     .observeSuccessWithReplay()
                     .compose(bindViewIoToMainComposer())
                     .map(command -> !DtlLocationHelper.checkLocation(MAX_DISTANCE, command.getResult()
                           .getCoordinates()
                           .asLatLng(), position.target, DistanceType.MILES));
            });
   }

   protected void onMerchantsLoaded(List<ThinMerchant> merchants) {
      getView().showProgress(false);
      getView().showButtonLoadMerchants(false);
      showPins(merchants);
      //
      locationInteractor.locationPipe()
            .observeSuccessWithReplay()
            .map(DtlLocationCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(location -> {
               if (location.getLocationSourceType() == LocationSourceType.FROM_MAP && getView().getMap()
                     .getCameraPosition().zoom < MapViewUtils.DEFAULT_ZOOM) getView().zoom(MapViewUtils.DEFAULT_ZOOM);
               //
               if (location.getLocationSourceType() != LocationSourceType.NEAR_ME)
                  getView().addLocationMarker(location.getCoordinates().asLatLng());
            });
   }

   @Override
   public void onListClicked() {
      History history = History.single(DtlMerchantsPath.getDefault());
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   @Override
   public void retryLoadMerchant() {
      if (actionParamsHolder == null) return;
      //
      merchantInteractor.merchantByIdHttpPipe().send(MerchantByIdParamsHolder.toAction(actionParamsHolder));
   }

   private void showPins(List<ThinMerchant> merchants) {
      getView().clearMap();
      Queryable.from(merchants).forEachR(merchant -> getView().addPin(merchant));
      getView().renderPins();
   }

   @Override
   public void onMapLoaded() {
      connectInteractors();
      //
      getFirstCenterLocation().compose(bindViewIoToMainComposer()).subscribe(getView()::centerIn);
      //
      showingLoadMerchantsButton().compose(bindView()).subscribe(show -> getView().showButtonLoadMerchants(show));
      //
      MapObservableFactory.createMarkerClickObservable(getView().getMap())
            .compose(bindView())
            .subscribe(marker -> getView().markerClick(marker));
      //
      gpsLocationDelegate.getLastKnownLocation()
            .compose(bindViewIoToMainComposer())
            .subscribe(location -> tryHideMyLocationButton(false), throwable -> tryHideMyLocationButton(true));

   }

   @Override
   public void applySearch(String query) {
      filterInteractor.filterDataPipe().send(DtlFilterDataAction.applySearch(query));
   }

   @Override
   public void locationChangeRequested() {
      History history = History.single(new DtlLocationChangePath());
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   @Override
   public void onMarkerClick(ThinMerchant merchant) {
      getView().showPinInfo(merchant);
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
      locationInteractor.locationPipe().send(DtlLocationCommand.change(mapSelectedLocation));
      //
      android.location.Location location = new android.location.Location("");
      location.setLatitude(latLng.latitude);
      location.setLongitude(latLng.longitude);
      merchantInteractor.merchantsActionPipe().send(DtlMerchantsAction.load(location));
   }
}
