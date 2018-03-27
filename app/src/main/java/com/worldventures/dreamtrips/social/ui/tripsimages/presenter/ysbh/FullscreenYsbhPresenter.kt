package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh

import com.worldventures.core.model.ShareType
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto
import com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate.DownloadImageDelegate
import rx.functions.Action2

import java.io.IOException

import javax.inject.Inject

class FullscreenYsbhPresenter(private val ysbhPhoto: YSBHPhoto) : Presenter<FullscreenYsbhPresenter.View>() {

   @Inject internal lateinit var downloadImageDelegate: DownloadImageDelegate

   override fun onViewTaken() {
      super.onViewTaken()
      view.setPhoto(ysbhPhoto)
   }

   fun onShareAction() {
      if (!isConnected) {
         reportNoConnectionWithOfflineErrorPipe(IOException())
         return
      }
      view.onShowShareOptions()
   }

   fun onShareOptionChosen(@ShareType type: String) {
      if (type == ShareType.EXTERNAL_STORAGE) {
         downloadImageDelegate.downloadImage(ysbhPhoto.url, bindViewToMainComposer(), Action2(this::handleError))
      } else {
         view.openShare(ysbhPhoto.url, ysbhPhoto.title, type)
      }
   }

   interface View : Presenter.View {
      fun setPhoto(photo: YSBHPhoto)

      fun openShare(url: String, shareText: String, @ShareType type: String)

      fun onShowShareOptions()
   }
}
