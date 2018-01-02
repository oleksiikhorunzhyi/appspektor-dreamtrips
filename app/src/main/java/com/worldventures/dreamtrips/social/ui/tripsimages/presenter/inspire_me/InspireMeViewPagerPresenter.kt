package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me

import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.BaseImageViewPagerPresenter
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.InspireMeViewPagerArgs
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.inspire_me.FullscreenInspireMeFragment
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

class InspireMeViewPagerPresenter(args: InspireMeViewPagerArgs) : BaseImageViewPagerPresenter<BaseImageViewPagerPresenter.View>(args.isLastPageReached, args.currentItemPosition) {

   private var randomSeed = args.randomSeed
   internal var currentItems = args.currentItems

   @field:Inject internal lateinit var tripImagesInteractor: TripImagesInteractor

   override val currentItemsSize: Int
      get() = currentItems.size

   override fun onViewTaken() {
      super.onViewTaken()
      subscribeToNewItems()
   }

   override fun initItems() {
      tripImagesInteractor.inspireMePhotosPipe
            .createObservableResult(GetInspireMePhotosCommand.cachedCommand())
            .compose(bindViewToMainComposer())
            .subscribe {
               currentItems = ArrayList(it.result)
               super.initItems()
            }
   }

   internal fun subscribeToNewItems() {
      tripImagesInteractor.inspireMePhotosPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetInspireMePhotosCommand>()
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
                  .onFail { inspireMePhotosCommand, throwable ->
                     loading = false
                     handleError(inspireMePhotosCommand, throwable)
                  })
   }

   override fun makeFragmentItems() =
      currentItems.map { FragmentItem(FullscreenInspireMeFragment::class.java, "", it) }.toList()

   override fun loadMore() =
      tripImagesInteractor.inspireMePhotosPipe
            .send(GetInspireMePhotosCommand.forPage(randomSeed, currentItems.size / GetYSBHPhotosCommand.PER_PAGE + 1))
}
