package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me

import com.worldventures.core.model.ShareType
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.ShareInspirationImageAnalyticAction
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration
import com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate.DownloadImageDelegate
import rx.functions.Action2

import java.io.IOException

import javax.inject.Inject

class FullscreenInspireMePresenter(private val inspiration: Inspiration) : Presenter<FullscreenInspireMePresenter.View>() {

   @Inject lateinit var downloadImageDelegate: DownloadImageDelegate

   override fun onViewTaken() {
      super.onViewTaken()
      view.setPhoto(inspiration)
   }

   fun onShareAction() = if (isConnected) view.onShowShareOptions()
      else reportNoConnectionWithOfflineErrorPipe(IOException())

   fun onShareOptionChosen(@ShareType type: String) {
      if (type == ShareType.EXTERNAL_STORAGE) {
         downloadImageDelegate.downloadImage(inspiration.url, bindViewToMainComposer(), Action2(this::handleError))
      } else {
         view.openShare(inspiration.url, "$inspiration.quote - $inspiration.author", type)
      }
      analyticsInteractor.analyticsActionPipe().send(ShareInspirationImageAnalyticAction())
   }

   interface View : Presenter.View {
      fun setPhoto(photo: Inspiration)

      fun openShare(url: String, shareText: String, @ShareType type: String)

      fun onShowShareOptions()
   }
}
