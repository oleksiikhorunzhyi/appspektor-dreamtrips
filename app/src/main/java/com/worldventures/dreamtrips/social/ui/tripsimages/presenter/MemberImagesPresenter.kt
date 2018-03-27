package com.worldventures.dreamtrips.social.ui.tripsimages.presenter

import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.social.ui.background_uploading.model.CompoundOperationState
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CheckVideoProcessingStatusCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesAddedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate.MediaRefresher
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs
import rx.Observable
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class MemberImagesPresenter(tripImagesArgs: TripImagesArgs) : TripImagesPresenter(tripImagesArgs) {

   @Inject lateinit var memberImagesRefresher: MediaRefresher

   override fun onResume() {
      super.onResume()
      subscribeToRefresher()
   }

   override fun onPause() {
      super.onPause()
      stopRefreshingMemberImages()
   }

   private fun subscribeToRefresher() {
      tripImagesInteractor.baseTripImagesPipe.observeSuccess()
            .compose<BaseMediaCommand>(bindUntilPauseIoToMainComposer())
            .subscribe { startRefreshingMemberImages() }
      memberImagesRefresher.newPhotosObservable
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(this::processNewPhotosFromRefresher,
                  { Timber.w(it, "Could not refresh trip images") })
      if (!loading) {
         startRefreshingMemberImages()
      }
   }

   fun onShowNewImagesClick() {
      val lastPhotos = ArrayList(memberImagesRefresher.lastPhotos)
      if (lastPhotos.size < tripImagesArgs.pageSize) {
         tripImagesInteractor.memberImagesAddedCommandPipe.send(MemberImagesAddedCommand(tripImagesArgs, lastPhotos))
         currentItems.addAll(0, lastPhotos)
         tripImagesInteractor.checkVideoProcessingStatusPipe.send(CheckVideoProcessingStatusCommand(currentItems))
         updateItemsInView()
         restartImageRefreshing()
      } else {
         reload()
         stopRefreshingMemberImages()
      }
      view.hideNewImagesButton()
   }

   private fun startRefreshingMemberImages() {
      if (memberImagesAreRefreshing) {
         return
      }
      memberImagesAreRefreshing = true
      memberImagesRefresher.startRefreshing(Observable.defer {
         if (currentItems.isEmpty()) {
            return@defer Observable.empty<BaseMediaEntity<*>>()
         }
         return@defer Observable.just(currentItems[0])
      }, tripImagesArgs)
   }

   private fun stopRefreshingMemberImages() {
      memberImagesRefresher.stopRefreshing()
      memberImagesAreRefreshing = false
   }

   private fun restartImageRefreshing() {
      stopRefreshingMemberImages()
      startRefreshingMemberImages()
   }

   private fun processNewPhotosFromRefresher(newPhotos: List<BaseMediaEntity<*>>) {
      val uploading = compoundOperationModels.count {
         val state = it.state()
         state == CompoundOperationState.STARTED || state == CompoundOperationState.FINISHED } > 0
      if (loading || uploading) {
         return
      }
      val newPhotosCount = newPhotos.count { !currentItems.contains(it) }
      if (newPhotosCount > 0) {
         view.showNewImagesButton(formatNewImagesString(newPhotosCount))
      }
   }

   private fun formatNewImagesString(newPhotosCount: Int): String {
      val photosCountString = newPhotosCount.toString()
      return when {
         newPhotosCount >= tripImagesArgs.pageSize -> context
               .getString(R.string.member_images_new_items_plus, photosCountString)
         newPhotosCount == 1 -> context.getString(R.string.member_images_new_item, photosCountString)
         else -> context.getString(R.string.member_images_new_items, photosCountString)
      }
   }
}
