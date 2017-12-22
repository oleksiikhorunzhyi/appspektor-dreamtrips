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

   internal var loading = false
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
                  }
                  .onFail { getYSBHPhotosCommand, throwable ->
                     loading = false
                     view.finishLoading()
                     handleError(getYSBHPhotosCommand, throwable)
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

   fun loadNext() {
      if (lastPageReached || loading || currentItems.isEmpty()) {
         return
      }
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
}
