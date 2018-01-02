package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me

import android.support.annotation.VisibleForTesting
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand
import icepick.State
import io.techery.janet.helper.ActionStateSubscriber
import java.util.ArrayList
import javax.inject.Inject

class InspireMePresenter : Presenter<InspireMePresenter.View>() {

   internal var randomSeed: Double = 0.0
   private var previousScrolledTotal = 0
   var loading = true
   var lastPageReached = false

   @JvmField @State internal var currentItems = arrayListOf<Inspiration>()

   @Inject internal lateinit var tripImagesInteractor: TripImagesInteractor

   override fun onViewTaken() {
      super.onViewTaken()
      if (randomSeed != 0.0) {
         randomSeed = Math.random() * 2 - 1
      }
      view.updatePhotos(currentItems)
      subscribeToNewItems()
      reload()
   }

   @VisibleForTesting
   fun subscribeToNewItems() {
      tripImagesInteractor.inspireMePhotosPipe
            .observe()
            .filter { !it.action.isFromCache }
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetInspireMePhotosCommand>()
                  .onStart {
                     loading = true
                     view.startLoading()
                  }
                  .onSuccess { command ->
                     loading = false
                     lastPageReached = command.lastPageReached()
                     view.finishLoading()

                     if (command.page == 1) {
                        currentItems.clear()
                     }
                     currentItems.addAll(command.result)
                     view.updatePhotos(ArrayList(currentItems))
                  }
                  .onFail { inspireMePhotosCommand, throwable ->
                     loading = false
                     view.finishLoading()
                     handleError(inspireMePhotosCommand, throwable)
                  })
   }

   fun reload() {
      loading = true
      randomSeed = Math.random() * 2 - 1
      tripImagesInteractor.inspireMePhotosPipe.send(GetInspireMePhotosCommand.forPage(randomSeed, 1))
   }

   fun onItemClick(entity: Inspiration) = view.openFullscreen(currentItems,
         randomSeed, lastPageReached, currentItems.indexOf(entity))

   fun scrolled(visibleItemCount: Int, totalItemCount: Int, firstVisibleItem: Int) {
      if (totalItemCount > previousScrolledTotal) {
         loading = false
         previousScrolledTotal = totalItemCount
      }
      if (!lastPageReached && !loading && currentItems.size > 0
            && totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD) {
         loadNext()
      }
   }

   internal fun loadNext() {
      loading = true
      tripImagesInteractor.inspireMePhotosPipe.send(GetInspireMePhotosCommand.forPage(randomSeed,
            currentItems.size / GetInspireMePhotosCommand.PER_PAGE + 1))
   }

   interface View : Presenter.View {
      fun openFullscreen(photos: List<Inspiration>?, randomSeed: Double, lastPageReached: Boolean, selectedItemIndex: Int)

      fun startLoading()

      fun finishLoading()

      fun updatePhotos(photoList: List<Inspiration>)
   }

   companion object {
      const val VISIBLE_THRESHOLD = 5
   }
}
