package com.worldventures.dreamtrips.modules.trips.presenter

import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.dreamtrips.core.rx.RxView
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.config.model.Configuration
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.trips.model.analytics.TripsFilterDataAnalyticsWrapper
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor
import com.worldventures.dreamtrips.modules.trips.service.analytics.TripItemAnalyticAction
import com.worldventures.dreamtrips.modules.trips.service.analytics.ViewDreamTripsAdobeAnalyticAction
import com.worldventures.dreamtrips.modules.trips.service.analytics.ViewDreamTripsApptentiveAnalyticAction
import com.worldventures.dreamtrips.modules.trips.service.analytics.ViewMapDreamTripsAnalyticAction
import com.worldventures.dreamtrips.modules.trips.service.analytics.ViewTripDetailsAnalyticAction
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsCommand
import com.worldventures.dreamtrips.modules.trips.service.command.TripFiltersAppliedCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.CreateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketBodyImpl
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand
import icepick.State
import io.techery.janet.ActionState
import io.techery.janet.helper.ActionStateSubscriber
import rx.Observable
import javax.inject.Inject

class TripListPresenter : Presenter<TripListPresenter.View>() {

   @Inject lateinit var feedInteractor: FeedInteractor
   @Inject lateinit var bucketInteractor: BucketInteractor
   @Inject lateinit var appConfigurationInteractor: AppConfigurationInteractor
   @Inject lateinit var tripsInteractor: TripsInteractor

   @JvmField @State var query = ""

   private var loading: Boolean = false
   private var noMoreItems = false

   override fun takeView(view: View) {
      super.takeView(view)
      analyticsInteractor.analyticsActionPipe().send(ViewDreamTripsApptentiveAnalyticAction())
      subscribeToLoadTrips()
      subscribeToFilterEvents()
      subscribeToLikesChanges()

      Observable.combineLatest<Configuration, List<TripModel>, List<Any>>(configObservable(), tripsObservable()
      ) { configuration, trips ->
         val items = mutableListOf<Any>()
         configuration.travelBannerRequirement?.let { if (it.enabled) items.add(it) }
         items.addAll(trips)
         items
      }.compose(bindViewToMainComposer()).subscribe { view.itemsChanged(it) }

      reload(true)
      appConfigurationInteractor.configurationPipe.send(ConfigurationCommand())
   }

   override fun onResume() {
      super.onResume()
      analyticsInteractor.analyticsActionPipe().send(ViewDreamTripsAdobeAnalyticAction())
   }

   private fun tripsObservable() = tripsInteractor.tripsPipe
         .observe()
         .filter { it.status == ActionState.Status.SUCCESS || it.status == ActionState.Status.PROGRESS }
         .map { it.action.items }

   private fun configObservable() = appConfigurationInteractor.configurationPipe.observeSuccess().map { it.result }

   fun reload(loadWithStatus: Boolean) {
      loading = true
      noMoreItems = false
      if (loadWithStatus) view.startLoading()
      loadTrips(true)
   }

   fun resetFilters() = tripsInteractor.tripFiltersAppliedPipe.send(TripFiltersAppliedCommand())

   private fun loadMore() = loadTrips(false)

   private fun loadTrips(refresh: Boolean) {
      tripsInteractor.tripFiltersAppliedPipe
            .observeSuccessWithReplay()
            .take(1)
            .subscribe { tripsInteractor.tripsPipe.send(GetTripsCommand(query, it.result, refresh)) }
   }

   fun onMenuInflated() {
      view.setQuery(query)
      subscribeToSearch()
   }

   private fun subscribeToSearch() {
      view.textChanges()
            .compose(bindView())
            .subscribe { search(it) }
   }

   private fun search(query: String) {
      this.query = query
      tripsInteractor.tripsPipe.cancelLatest()
      reload(true)
   }

   private fun subscribeToFilterEvents() {
      tripsInteractor.tripFiltersAppliedPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe { reload(true) }
   }

   private fun subscribeToLoadTrips() {
      tripsInteractor.tripsPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetTripsCommand>()
                  .onSuccess { noMoreItems = !it.hasMore() }
                  .onFinish {
                     loading = false
                     view.finishLoading()
                  }
                  .onFail(this::handleError))
   }

   fun likeItem(trip: TripModel) {
      sendAnalyticAction(TripItemAnalyticAction.likeAction(trip.tripId, trip.name))
      feedInteractor.changeFeedEntityLikedStatusPipe().send(ChangeFeedEntityLikedStatusCommand(trip))
   }

   private fun subscribeToLikesChanges() {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<ChangeFeedEntityLikedStatusCommand>()
                  .onSuccess { view.itemLiked(it.result) }
                  .onFail(this::handleError))
   }

   fun hideTripRequirement() = appConfigurationInteractor.configurationPipe.send(ConfigurationCommand(hideTravelConfig = true))

   fun addItemToBucket(tripModel: TripModel) {
      sendAnalyticAction(TripItemAnalyticAction.addToBucketListAction(tripModel.tripId, tripModel.name))
      if (!tripModel.isInBucketList) {
         bucketInteractor.createPipe()
               .createObservable(CreateBucketItemCommand(ImmutableBucketBodyImpl.builder()
                     .type("trip")
                     .id(tripModel.tripId)
                     .build()))
               .compose(bindViewToMainComposer())
               .subscribe(ActionStateSubscriber<CreateBucketItemCommand>()
                     .onSuccess {
                        tripModel.isInBucketList = true
                        view.dataSetChanged()
                        view.showItemAddedToBucketList(it.result)
                     }
                     .onFail { createBucketItemCommand, throwable ->
                        tripModel.isInBucketList = !tripModel.isInBucketList
                        handleError(createBucketItemCommand, throwable)
                     })
      } else {
         tripModel.isInBucketList = !tripModel.isInBucketList
         view.dataSetChanged()
         view.showErrorMessage()
      }
   }

   fun openMap() {
      sendAnalyticAction(ViewMapDreamTripsAnalyticAction())
      view.openMap()
   }

   fun openTrip(tripModel: TripModel, isSearchOpened: Boolean) {
      view.moveToTripDetails(tripModel)
      trackTripOpened(tripModel, isSearchOpened)
   }

   private fun trackTripOpened(tripModel: TripModel, isSearchOpened: Boolean) {
      tripsInteractor.tripFiltersAppliedPipe
            .observeSuccessWithReplay()
            .take(1)
            .subscribe {
               val analyticAction = ViewTripDetailsAnalyticAction(tripModel.tripId,
                     tripModel.name, if (isSearchOpened) query else "", TripsFilterDataAnalyticsWrapper(it.result))
               sendAnalyticAction(analyticAction)
            }
   }

   private fun sendAnalyticAction(action: BaseAnalyticsAction) {
      analyticsInteractor.analyticsActionPipe().send(action)
   }

   fun scrolled() {
      if (!loading && !noMoreItems) {
         loading = true
         loadMore()
      }
   }

   interface View : RxView {
      fun dataSetChanged()

      fun showErrorMessage()

      fun startLoading()

      fun finishLoading()

      fun textChanges(): Observable<String>

      fun itemsChanged(items: List<Any>)

      fun itemLiked(feedEntity: FeedEntity)

      fun showItemAddedToBucketList(bucketItem: BucketItem)

      fun moveToTripDetails(tripModel: TripModel)

      fun openMap()

      fun setQuery(query: String)
   }
}
