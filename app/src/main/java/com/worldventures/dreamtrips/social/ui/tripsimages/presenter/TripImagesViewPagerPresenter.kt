package com.worldventures.dreamtrips.social.ui.tripsimages.presenter

import android.os.Parcelable
import android.support.v4.app.Fragment
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.NotificationFeedInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.command.MarkNotificationAsReadCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.model.MediaEntityType
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImageArgsFilterFunc
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.TripImagesCommandFactory
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesFullscreenArgs
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.FullscreenPhotoFragment
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.FullscreenVideoFragment
import com.worldventures.dreamtrips.social.ui.video.view.custom.VideoPlayerHolder
import io.techery.janet.helper.ActionStateSubscriber
import rx.Observable
import javax.inject.Inject

class TripImagesViewPagerPresenter(args: TripImagesFullscreenArgs) : BaseImageViewPagerPresenter<BaseImageViewPagerPresenter.View>(args.isLastPageReached, args.currentItem) {
   @Inject internal lateinit var tripImagesCommandFactory: TripImagesCommandFactory
   @Inject internal lateinit var tripImagesInteractor: TripImagesInteractor
   @Inject internal lateinit var feedInteractor: FeedInteractor
   @Inject internal lateinit var notificationFeedInteractor: NotificationFeedInteractor
   @Inject internal lateinit var videoPlayerHolder: VideoPlayerHolder

   private var baseMediaEntities: MutableList<BaseMediaEntity<*>> = mutableListOf()
   internal var tripImagesArgs: TripImagesArgs? = null
   internal var notificationId: Int
   private var displayOnlyCachedImages = false

   override val currentItemsSize: Int
      get() = baseMediaEntities.size

   init {
      if (args.tripImagesArgs == null) {
         displayOnlyCachedImages = true
         this.baseMediaEntities = args.mediaEntityList
      } else {
         this.tripImagesArgs = args.tripImagesArgs
      }
      this.notificationId = args.notificationId
   }

   override fun onViewTaken() {
      super.onViewTaken()
      subscribeToTripImages()
      if (notificationId != 0) {
         notificationFeedInteractor.markNotificationPipe().send(MarkNotificationAsReadCommand(notificationId))
      }
      Observable.merge<FeedEntity>(feedInteractor.deleteVideoPipe().observeSuccess().map(DeleteVideoCommand::getResult),
            tripImagesInteractor.deletePhotoPipe.observeSuccess().map(DeletePhotoCommand::getResult))
            .compose(bindViewToMainComposer())
            .subscribe { itemDeleted() }
   }

   private fun itemDeleted() = view.goBack()

   override fun pageSelected(position: Int) {
      videoPlayerHolder.releaseCurrentVideo()
      super.pageSelected(position)
   }

   override fun initItems() {
      if (displayOnlyCachedImages) {
         super.initItems()
         return
      }
      tripImagesArgs?.apply {
         tripImagesInteractor.baseTripImagesPipe
               .createObservableResult(tripImagesCommandFactory.provideCommandCacheOnly(this))
               .compose(bindViewToMainComposer())
               .subscribe {
                  baseMediaEntities = ArrayList(it.result)
                  super.initItems()
               }
      }
   }

   private fun subscribeToTripImages() {
      if (displayOnlyCachedImages) {
         return
      }
      tripImagesArgs?.apply {
         tripImagesInteractor.baseTripImagesPipe
               .observe()
               .filter(TripImageArgsFilterFunc(this))
               .compose(bindViewToMainComposer())
               .subscribe(ActionStateSubscriber<BaseMediaCommand>()
                     .onStart { loading = true }
                     .onFail { getYSBHPhotosCommand, throwable ->
                        loading = false
                        handleError(getYSBHPhotosCommand, throwable)
                     }
                     .onSuccess { baseTripImagesCommand ->
                        loading = false
                        baseMediaEntities.addAll(baseTripImagesCommand.result)
                        view.setItems(makeFragmentItems())
                     }
               )
      }
   }

   override fun makeFragmentItems(): List<FragmentItem> {
      return baseMediaEntities.filter { it.type != MediaEntityType.UNKNOWN }
            .map { entity ->
               val mediaFragmentClass: Class<out Fragment> =
                     if (entity.type == MediaEntityType.PHOTO) {
                        FullscreenPhotoFragment::class.java
                     } else {
                        FullscreenVideoFragment::class.java
                     }
               val item = entity.item
               if (item is Parcelable) {
                  FragmentItem(mediaFragmentClass, "", item)
               } else {
                  throw Exception("Item is not Parcelable")
               }
            }
            .toList()
   }

   override fun loadMore() {
      if (!displayOnlyCachedImages) {
         tripImagesArgs?.apply {
            tripImagesInteractor.baseTripImagesPipe
                  .send(tripImagesCommandFactory.provideLoadMoreCommand(this, baseMediaEntities))
         }
      }
   }
}
