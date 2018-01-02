package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh

import android.support.annotation.VisibleForTesting
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.BaseImageViewPagerPresenter
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.YsbhPagerArgs
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh.FullscreenYsbhFragment
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

class YSBHViewPagerPresenter(args: YsbhPagerArgs) : BaseImageViewPagerPresenter<BaseImageViewPagerPresenter.View>(args.isLastPageReached, args.currentItemPosition) {

   internal var currentItems = args.currentItems

   @Inject internal lateinit var tripImagesInteractor: TripImagesInteractor

   override val currentItemsSize: Int
      get() = currentItems.size

   override fun onViewTaken() {
      super.onViewTaken()
      subscribeToNewItems()
   }

   override fun initItems() {
      tripImagesInteractor.ysbhPhotosPipe
            .createObservableResult(GetYSBHPhotosCommand.cachedCommand())
            .compose(bindViewToMainComposer())
            .subscribe {
               currentItems = it.result
               super.initItems()
            }
   }

   @VisibleForTesting
   fun subscribeToNewItems() {
      tripImagesInteractor.ysbhPhotosPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetYSBHPhotosCommand>()
                  .onStart { loading = true }
                  .onSuccess {
                     loading = false
                     lastPageReached = it.lastPageReached()
                     if (it.page == 1) {
                        currentItems.clear()
                     }
                     currentItems.addAll(it.result)
                     view.setItems(makeFragmentItems())
                  }
                  .onFail { command, throwable ->
                     loading = false
                     handleError(command, throwable)
                  })
   }

   override fun makeFragmentItems() =
      currentItems.map { FragmentItem(FullscreenYsbhFragment::class.java, "", it) }.toList()

   override fun loadMore() =
      tripImagesInteractor.ysbhPhotosPipe
            .send(GetYSBHPhotosCommand.commandForPage(currentItems.size / GetYSBHPhotosCommand.PER_PAGE + 1))
}
