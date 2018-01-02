package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh

import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageItemViewEvent
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand
import icepick.State
import io.techery.janet.helper.ActionStateSubscriber
import java.util.ArrayList
import javax.inject.Inject

class YSBHPresenter : Presenter<YSBHPresenter.View>() {

   private var previousScrolledTotal = 0
   internal var loading = true
   internal var lastPageReached = false

   @JvmField @State var currentItems = ArrayList<YSBHPhoto>()

   @Inject internal lateinit var tripImagesInteractor: TripImagesInteractor

   override fun onViewTaken() {
      super.onViewTaken()
      view.updatePhotos(currentItems)
      subscribeToNewItems()
      reload()
   }

   fun subscribeToNewItems() {
      tripImagesInteractor.ysbhPhotosPipe
            .observe()
            .filter { !it.action.isFromCache }
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetYSBHPhotosCommand>()
                  .onStart {
                     loading = true
                     view.startLoading()
                  }
                  .onFail { getYSBHPhotosCommand, throwable ->
                     loading = false
                     view.finishLoading()
                     handleError(getYSBHPhotosCommand, throwable)
                  }
                  .onSuccess { getYSBHPhotosCommand ->
                     view.finishLoading()
                     loading = false
                     lastPageReached = getYSBHPhotosCommand.lastPageReached()
                     currentItems = ArrayList(currentItems)
                     if (getYSBHPhotosCommand.page == 1) {
                        currentItems.clear()
                     }
                     currentItems.addAll(getYSBHPhotosCommand.result)
                     view.updatePhotos(currentItems)
                  })
   }

   fun reload() {
      loading = true
      tripImagesInteractor.ysbhPhotosPipe.send(GetYSBHPhotosCommand.commandForPage(1))
   }

   fun onItemClick(entity: YSBHPhoto) {
      view.openFullscreen(ArrayList(currentItems), lastPageReached, currentItems.indexOf(entity))
      analyticsInteractor.analyticsActionPipe().send(TripImageItemViewEvent(entity.id.toString()))
   }

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
      tripImagesInteractor.ysbhPhotosPipe
            .send(GetYSBHPhotosCommand.commandForPage(currentItems.size / GetYSBHPhotosCommand.PER_PAGE + 1))
   }

   interface View : Presenter.View {
      fun startLoading()

      fun finishLoading()

      fun openFullscreen(photos: List<YSBHPhoto>?, lastPageReached: Boolean, selectedItemIndex: Int)

      fun updatePhotos(photoList: List<YSBHPhoto>)
   }

   companion object {

      const val VISIBLE_THRESHOLD = 5
   }
}
