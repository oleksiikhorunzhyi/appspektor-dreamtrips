package com.worldventures.dreamtrips.modules.trips.presenter

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.trips.model.map.Pin
import com.worldventures.dreamtrips.modules.trips.model.map.TripClusterItem
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor
import com.worldventures.dreamtrips.modules.trips.service.command.CheckTripsByUidCommand
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsByUidCommand
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsLocationsCommand
import com.worldventures.dreamtrips.social.util.event_delegate.DrawerOpenedEventDelegate
import icepick.State
import io.techery.janet.helper.ActionStateSubscriber
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.ArrayList
import javax.inject.Inject

class TripMapPresenter : Presenter<TripMapPresenter.View>() {

   @JvmField @State var query = ""

   @Inject lateinit var tripsInteractor: TripsInteractor
   @Inject lateinit var drawerOpenedEventDelegate: DrawerOpenedEventDelegate

   private val pins: MutableList<Pin>
   private val existingMarkers: MutableList<Marker>

   init {
      pins = ArrayList()
      existingMarkers = ArrayList()
   }

   override fun takeView(view: View) {
      super.takeView(view)
      subscribeToSideNavigationClicks()
      subscribeToMapReloading()
      subscribeToFilterEvents()
   }

   override fun onResume() {
      super.onResume()
      subscribeToTripLoading()
   }

   private fun subscribeToFilterEvents() {
      tripsInteractor.tripFiltersAppliedPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe { reloadMapObjects() }
   }

   private fun subscribeToSideNavigationClicks() {
      drawerOpenedEventDelegate.observable
            .compose(bindViewToMainComposer<Any>())
            .subscribe { removeInfoIfNeeded() }
   }

   fun onCameraChanged() = removeInfoIfNeeded()

   fun onMenuInflated() {
      view.setQuery(query)
      subscribeToSearch()
   }

   private fun subscribeToSearch() {
      view.textChanges()
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe { search(it) }
   }

   fun search(query: String) {
      this.query = query
      removeInfoIfNeeded()
      reloadMapObjects()
   }

   fun onMapLoaded() = reloadMapObjects()

   fun onMarkerClicked(marker: Marker) {
      val (_, tripUids) = pins.firstOrNull {
         it.coordinates.lat == marker.position.latitude
               && it.coordinates.lng == marker.position.longitude
      } ?: return

      tripsInteractor.checkTripsByUidPipe
            .createObservableResult(CheckTripsByUidCommand(tripUids))
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe { cacheIsChecked(marker, tripUids, it.result) }
   }

   private fun cacheIsChecked(marker: Marker, tripUids: List<String>, cacheExists: Boolean) {
      if (!isConnected && !cacheExists) reportNoConnection()
      else {
         view.setSelectedLocation(marker.position)
         view.scrollCameraToPin(tripUids.size)
         updateExistsMarkers(view.markers)
         view.showInfoContainer()
         view.updateContainerParams(tripUids.size)
         addAlphaToMarkers(marker)
         loadTrips(tripUids)
      }
   }

   private fun updateExistsMarkers(markers: List<Marker>) {
      existingMarkers.apply {
         clear()
         addAll(markers)
      }
   }

   private fun addAlphaToMarkers(marker: Marker) {
      for (existMarker in existingMarkers.filter { marker != it }) existMarker.alpha = ALPHA_INACTIVE
   }

   fun removeInfoIfNeeded() {
      view?.let {
         it.removeTripsPopupInfo()
         removeAlphaFromMarkers()
         cancelLatestTripAction()
      }
   }

   private fun removeAlphaFromMarkers() {
      for (existMarker in existingMarkers) existMarker.alpha = ALPHA_ACTIVE
   }

   private fun reloadMapObjects() {
      tripsInteractor.tripFiltersAppliedPipe
            .observeSuccessWithReplay()
            .take(1)
            .subscribe {
               tripsInteractor.mapObjectsActionPipe
                     .send(GetTripsLocationsCommand(query, it.result))
            }
   }

   private fun subscribeToMapReloading() {
      tripsInteractor.mapObjectsActionPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetTripsLocationsCommand>()
                  .onProgress { action, _ -> actionSucceed(action) }
                  .onSuccess { actionSucceed(it) }
                  .onFail(this::handleError))
   }

   private fun actionSucceed(action: GetTripsLocationsCommand) {
      updateMapObjectsList(action.items)
      onTripMapObjectsLoaded(action.items.map { TripClusterItem(it) })
   }

   private fun updateMapObjectsList(pins: List<Pin>) {
      this.pins.apply {
         clear()
         addAll(pins)
      }
   }

   private fun onTripMapObjectsLoaded(tripClusterItems: List<TripClusterItem>) {
      view.clearItems()
      view.addItems(tripClusterItems)
   }

   private fun loadTrips(tripUids: List<String>) {
      tripsInteractor.tripsByUidPipe.send(GetTripsByUidCommand(tripUids))
   }

   private fun subscribeToTripLoading() {
      tripsInteractor.tripsByUidPipe
            .observe()
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(ActionStateSubscriber<GetTripsByUidCommand>()
                  .onProgress { action, _ -> onTripsLoaded(action.items) }
                  .onSuccess { onTripsLoaded(it.items) }
                  .onFail { action, e ->
                     if (!action.hasValidCachedItems()) view.removeTripsPopupInfo()
                     handleError(action, e)
                  })
   }

   private fun onTripsLoaded(trips: List<TripModel>) = view.moveTo(trips)

   private fun cancelLatestTripAction() = tripsInteractor.tripsByUidPipe.cancelLatest()

   interface View : Presenter.View {

      val markers: List<Marker>

      fun moveTo(tripList: List<TripModel>)

      fun removeTripsPopupInfo()

      fun setSelectedLocation(latLng: LatLng)

      fun updateContainerParams(tripCount: Int)

      fun scrollCameraToPin(size: Int)

      fun showInfoContainer()

      fun addItems(tripClusterItems: List<TripClusterItem>)

      fun clearItems()

      fun textChanges(): Observable<String>

      fun setQuery(query: String)
   }

   companion object {
      val ALPHA_INACTIVE = 0.6f
      val ALPHA_ACTIVE = 1f
   }
}
