package com.worldventures.dreamtrips.social.ui.membership.presenter

import com.worldventures.core.modules.video.utils.CachedModelHelper
import com.worldventures.core.service.CachedEntityDelegate
import com.worldventures.core.service.CachedEntityInteractor
import com.worldventures.core.service.command.CachedEntityCommand
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.core.ui.util.permission.PermissionSubscriber
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast
import com.worldventures.dreamtrips.social.ui.membership.service.PodcastsInteractor
import com.worldventures.dreamtrips.social.ui.membership.service.command.GetPodcastsCommand
import com.worldventures.dreamtrips.social.ui.podcast_player.service.ViewPodcastAnalyticsAction

import java.util.ArrayList

import javax.inject.Inject

import io.techery.janet.helper.ActionStateSubscriber
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

import com.worldventures.core.ui.util.permission.PermissionConstants.WRITE_EXTERNAL_STORAGE
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader
import io.techery.janet.ActionState
import org.jetbrains.annotations.NotNull

class PodcastsPresenter<T : PodcastsPresenter.View> : Presenter<T>() {

   @field:Inject lateinit var podcastsInteractor: PodcastsInteractor
   @field:Inject lateinit var cachedEntityInteractor: CachedEntityInteractor
   @field:Inject lateinit var cachedEntityDelegate: CachedEntityDelegate
   @field:Inject lateinit var cachedModelHelper: CachedModelHelper
   @field:Inject lateinit var permissionDispatcher: PermissionDispatcher

   override fun takeView(view: T) {
      super.takeView(view)
      subscribeToApiUpdates()
      subscribeToCachingStatusUpdates()
      onRefresh()
   }

   fun onLoadNextPage() {
      podcastsInteractor.podcastsActionPipe.send(GetPodcastsCommand())
   }

   fun onRefresh() {
      podcastsInteractor.podcastsActionPipe.send(GetPodcastsCommand(true))
   }

   private fun subscribeToApiUpdates() {
      podcastsInteractor.podcastsActionPipe.observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetPodcastsCommand>()
                  .onStart { view.startLoading() }
                  .onProgress { command, progress -> updatePodcasts(command.getItems()) }
                  .onSuccess { podcastsLoaded(it.getItems(), it.hasMore()) }
                  .onFail { command, error -> this.podcastsLoadingFailed(command, error) })
   }

   private fun podcastsLoaded(loadedPodcasts: List<Podcast>, hasMoreElement: Boolean) {
      view.finishLoading(hasMoreElement)
      updatePodcasts(loadedPodcasts)
   }

   private fun updatePodcasts(newPodcasts: List<Podcast>) {
      val items = ArrayList<Any>()
      if (!newPodcasts.isEmpty()) items.add(MediaHeader(context.getString(R.string.recently_added)))
      items.addAll(newPodcasts)
      view.setItems(items)
   }

   private fun podcastsLoadingFailed(command: GetPodcastsCommand, error: Throwable) {
      super.handleError(command, error)
      view.finishLoading(true)
   }

   private fun subscribeToCachingStatusUpdates() {
      Observable.merge<ActionState<out CachedEntityCommand>>(
            cachedEntityInteractor.downloadCachedModelPipe.observe(),
            cachedEntityInteractor.deleteCachedModelPipe.observe())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .map { it.action.getCachedModel() }
            .subscribe { view.notifyItemChanged(Podcast(fileUrl = it.uuid)) }
   }

   fun onDownloadPodcastRequired(podcast: Podcast) {
      val cachedModel = podcast.cachedModel
      permissionDispatcher.requestPermission(WRITE_EXTERNAL_STORAGE, false)
            .compose(bindView())
            .subscribe(PermissionSubscriber()
                  .onPermissionGrantedAction { cachedEntityDelegate.startCaching(cachedModel, cachedModelHelper.getPodcastPath(cachedModel)) })
   }

   fun onDeletePodcastRequired(podcast: Podcast) {
      view.showDeleteDialog(podcast)
   }

   fun onDeletePodcastAccepted(podcast: Podcast) {
      cachedEntityDelegate.deleteCache(podcast.cachedModel, cachedModelHelper.getPodcastPath(podcast.cachedModel))
   }

   fun onCancelPodcastRequired(entity: Podcast) {
      view.onCancelDialog(entity)
   }

   fun onCancelPodcastAccepted(podcast: Podcast) {
      cachedEntityDelegate.cancelCaching(podcast.cachedModel, cachedModelHelper.getPodcastPath(podcast.cachedModel))
   }

   fun play(podcast: Podcast) {
      val entity = podcast.cachedModel

      if (cachedModelHelper.isCachedPodcast(entity)) {
         activityRouter.openPodcastPlayer(cachedModelHelper.getPodcastPath(entity), podcast.title)
      } else {
         activityRouter.openPodcastPlayer(podcast.fileUrl, podcast.title)
      }
   }

   fun track() {
      analyticsInteractor.analyticsActionPipe().send(ViewPodcastAnalyticsAction())
   }

   interface View : Presenter.View {

      fun startLoading()

      fun finishLoading(noMoreElements: Boolean)

      fun setItems(@NotNull items: List<*>)

      fun notifyItemChanged(podcastUrl: Podcast?)

      fun showDeleteDialog(podcast: Podcast)

      fun onCancelDialog(podcast: Podcast)
   }
}
