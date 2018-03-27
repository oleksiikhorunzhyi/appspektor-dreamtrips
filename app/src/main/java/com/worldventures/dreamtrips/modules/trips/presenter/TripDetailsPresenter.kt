package com.worldventures.dreamtrips.modules.trips.presenter

import com.worldventures.core.model.session.Feature
import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.trips.model.ContentItem
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor
import com.worldventures.dreamtrips.modules.trips.service.analytics.BookItAction
import com.worldventures.dreamtrips.modules.trips.service.analytics.ViewDreamTripsApptentiveAnalyticAction
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripDetailsCommand
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripViewPagerBundle
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.CreateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketBodyImpl
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

class TripDetailsPresenter(var trip: TripModel) : Presenter<TripDetailsPresenter.View>() {

   @Inject lateinit var tripsInteractor: TripsInteractor
   @Inject lateinit var staticPageProvider: StaticPageProvider
   @Inject lateinit var bucketInteractor: BucketInteractor
   @Inject lateinit var feedInteractor: FeedInteractor

   override fun takeView(view: View) {
      super.takeView(view)
      analyticsInteractor.analyticsActionPipe().send(ViewDreamTripsApptentiveAnalyticAction())
      subscribeToLikesChanges()
      subscribeForTripsDetails()
      loadTripDetails()
   }

   override fun onResume() {
      super.onResume()
      view.setup(trip)
      val canBook = featureManager.available(Feature.BOOK_TRIP)
      if (!canBook) view.showSignUp()
      if (trip.isSoldOut) view.soldOutTrip()
      else if (!canBook || trip.isPlatinum && !account.isPlatinum) view.disableBookIt()
   }

   override fun onMenuPrepared() = view.setup(trip)

   fun addTripToBucket() {
      bucketInteractor.createPipe()
            .createObservable(CreateBucketItemCommand(ImmutableBucketBodyImpl.builder()
                  .type("trip")
                  .id(trip.tripId)
                  .build()))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<CreateBucketItemCommand>()
                  .onSuccess {
                     trip.isInBucketList = true
                     view.setup(trip)
                     view.tripAddedToBucketItem(it.result)
                  }
                  .onFail(this::handleError))
   }

   fun likeTrip() = feedInteractor.changeFeedEntityLikedStatusPipe().send(ChangeFeedEntityLikedStatusCommand(trip))

   private fun subscribeToLikesChanges() {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<ChangeFeedEntityLikedStatusCommand>()
                  .onSuccess { likeStatusChanged(it) }
                  .onFail(this::handleError))
   }

   private fun likeStatusChanged(command: ChangeFeedEntityLikedStatusCommand) {
      if (command.result.uid == trip.uid) {
         trip.syncLikeState(command.result)
         view.setup(trip)
         if (view.isVisibleOnScreen) view.tripLiked(trip)
      }
   }

   fun loadTripDetails() = tripsInteractor.detailsPipe.send(GetTripDetailsCommand(trip.uid))

   private fun subscribeForTripsDetails() {
      tripsInteractor.detailsPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetTripDetailsCommand>()
                  .onProgress { command, _ -> tripLoaded(command.cachedModel) }
                  .onSuccess {
                     tripLoaded(it.result)
                     analyticsInteractor.analyticsActionPipe().send(ViewDreamTripsApptentiveAnalyticAction())
                  }
                  .onFail { command, e ->
                     if (command.cachedModel == null || command.cachedModel?.content == null) view.setContent(null)
                     handleError(command, e)
                  })
   }

   private fun tripLoaded(tripModel: TripModel?) {
      tripModel?.apply {
         trip = tripModel
         view.setup(trip)
         view.setContent(tripModel.content)
      }
   }

   fun onItemClick(position: Int) = view.openFullscreen(TripViewPagerBundle(trip.imageUrls, position))

   fun actionBookIt() {
      view.openBookIt(staticPageProvider.getBookingPageUrl(trip.tripId))
      analyticsInteractor.analyticsActionPipe().send(BookItAction(trip.tripId, trip.name))
   }

   interface View : Presenter.View {
      fun setup(tripModel: TripModel)

      fun tripAddedToBucketItem(bucketItem: BucketItem)

      fun tripLiked(tripModel: TripModel)

      fun setContent(contentItems: List<ContentItem>?)

      fun disableBookIt()

      fun soldOutTrip()

      fun showSignUp()

      fun openFullscreen(data: TripViewPagerBundle)

      fun openBookIt(url: String)
   }
}
