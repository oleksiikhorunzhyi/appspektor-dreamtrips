package com.worldventures.dreamtrips.modules.trips.presenter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.utils.delegate.DrawerOpenedEventDelegate;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.command.CheckTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsLocationsCommand;
import com.worldventures.dreamtrips.modules.trips.delegate.TripFilterEventDelegate;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
import com.worldventures.dreamtrips.modules.trips.model.TripClusterItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.service.TripMapInteractor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class TripMapPresenter extends Presenter<TripMapPresenter.View> {

   @State String query;

   @Inject TripMapInteractor tripMapInteractor;
   @Inject TripFilterEventDelegate tripFilterEventDelegate;
   @Inject DrawerOpenedEventDelegate drawerOpenedEventDelegate;

   private List<Pin> pins;
   private List<Marker> exisingMarkers;

   public TripMapPresenter() {
      pins = new ArrayList<>();
      exisingMarkers = new ArrayList<>();
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      subscribeToSideNavigationClicks();
      subscribeToMapReloading();
      subscribeToFilterEvents();
      subscribeToTripLoading();
   }

   private void subscribeToFilterEvents() {
      tripFilterEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(tripsFilterData -> reloadMapObjects());
   }

   private void subscribeToSideNavigationClicks() {
      drawerOpenedEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(event -> removeInfoIfNeeded());
   }

   public void onCameraChanged() {
      removeInfoIfNeeded();
   }

   public String getQuery() {
      return query;
   }

   public void onMenuInflated() {
      subscribeToSearch();
   }

   private void subscribeToSearch() {
      view.textChanges()
            .compose(bindView())
            .subscribe(this::search);
   }

   public void search(String query) {
      this.query = query;
      //
      removeInfoIfNeeded();
      reloadMapObjects();
   }

   public void onMapLoaded() {
      reloadMapObjects();
   }

   public void onMarkerClicked(Marker marker) {
      Pin holder = Queryable.from(pins)
            .firstOrDefault(pin -> pin.getCoordinates().getLat() == marker.getPosition().latitude
                  && pin.getCoordinates().getLng() == marker.getPosition().longitude);
      if (holder == null) return;

      List<String> tripUids = holder.getTripUids();
      tripMapInteractor.checkTripsByUidPipe()
            .createObservableResult(new CheckTripsByUidCommand(tripUids))
            .compose(bindViewToMainComposer())
            .subscribe(cacheExistsCommand -> cacheIsChecked(marker, tripUids, cacheExistsCommand.getResult()));
   }

   private void cacheIsChecked(Marker marker, List<String> tripUids, boolean cacheExists) {
      if (!isConnected() && !cacheExists) {
         reportNoConnection();
      } else {
         view.setSelectedLocation(marker.getPosition());
         view.scrollCameraToPin(tripUids.size());
         updateExistsMarkers(view.getMarkers());

         view.showInfoContainer();
         view.updateContainerParams(tripUids.size());

         addAlphaToMarkers(marker);
         loadTrips(tripUids);
      }
   }

   private void updateExistsMarkers(List<Marker> markers) {
      exisingMarkers.clear();
      exisingMarkers.addAll(markers);
   }

   private void addAlphaToMarkers(Marker marker) {
      Queryable.from(exisingMarkers)
            .filter(existMarker -> !marker.equals(existMarker))
            .forEachR(existMarker -> existMarker.setAlpha(0.6f));
   }

   public void removeInfoIfNeeded() {
      if (view != null) {
         view.removeTripsPopupInfo();
         removeAlphaFromMarkers();
         cancelLatestTripAction();
      }
   }

   private void removeAlphaFromMarkers() {
      Queryable.from(exisingMarkers).forEachR(existMarker -> existMarker.setAlpha(1f));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Map reloading
   ///////////////////////////////////////////////////////////////////////////

   public void reloadMapObjects() {
      tripFilterEventDelegate.last().subscribe(tripsFilterData -> tripMapInteractor
            .mapObjectsPipe().send(new GetTripsLocationsCommand(query, tripsFilterData)));
   }

   private void subscribeToMapReloading() {
      tripMapInteractor.mapObjectsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetTripsLocationsCommand>()
                  .onProgress((action, progress) -> actionSucceed(action))
                  .onSuccess(this::actionSucceed)
                  .onFail((action, e) -> {
                     Timber.e(action.getErrorMessage(), e);
                     handleError(action, e);
                  }));
   }

   private void actionSucceed(GetTripsLocationsCommand action) {
      updateMapObjectsList(action.getItems());
      onTripMapObjectsLoaded(Queryable.from(action.getItems()).map(TripClusterItem::new).toList());
   }

   private void updateMapObjectsList(List<Pin> pins) {
      this.pins.clear();
      this.pins.addAll(pins);
   }

   private void onTripMapObjectsLoaded(List<TripClusterItem> tripClusterItems) {
      view.clearItems();
      view.addItems(tripClusterItems);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Trip loading
   ///////////////////////////////////////////////////////////////////////////

   private void loadTrips(List<String> tripUids) {
      tripMapInteractor.tripsByUidPipe().send(new GetTripsByUidCommand(tripUids));
   }

   private void subscribeToTripLoading() {
      tripMapInteractor.tripsByUidPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetTripsByUidCommand>()
                  .onProgress((action, progress) -> onTripsLoaded(action.getItems()))
                  .onSuccess(action -> onTripsLoaded(action.getItems()))
                  .onFail((action, e) -> {
                     Timber.e(action.getErrorMessage(), e);
                     if (!action.hasValidCachedItems()) view.removeTripsPopupInfo();
                     handleError(action, e);
                  }));
   }

   private void onTripsLoaded(List<TripModel> trips) {
      view.moveTo(trips);
   }

   private void cancelLatestTripAction() {
      tripMapInteractor.tripsByUidPipe().cancelLatest();
   }

   public interface View extends Presenter.View {

      void moveTo(List<TripModel> tripList);

      void removeTripsPopupInfo();

      void setSelectedLocation(LatLng latLng);

      GoogleMap getMap();

      void updateContainerParams(int tripCount);

      void scrollCameraToPin(int size);

      void showInfoContainer();

      void addItems(List<TripClusterItem> tripClusterItems);

      void clearItems();

      List<Marker> getMarkers();

      Observable<String> textChanges();
   }
}
