package com.worldventures.dreamtrips.social.ui.membership.presenter

import android.content.ActivityNotFoundException

import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.utils.CachedModelHelper
import com.worldventures.core.service.CachedEntityDelegate
import com.worldventures.core.service.CachedEntityInteractor
import com.worldventures.core.service.command.CachedEntityCommand
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.core.ui.util.permission.PermissionSubscriber
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.core.rx.RxView
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader
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
      podcastsInteractor.podcastsActionPipe.send(GetPodcastsCommand.loadMore())
   }

   fun onRefresh() {
      podcastsInteractor.podcastsActionPipe.send(GetPodcastsCommand.refresh())
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
      items.add(MediaHeader(context.getString(R.string.recently_added)))
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
            .subscribe { view.notifyItemChanged(it) }
   }

   fun onDownloadPodcastRequired(entity: CachedModel) {
      permissionDispatcher.requestPermission(WRITE_EXTERNAL_STORAGE, false)
            .compose(bindView())
            .subscribe(PermissionSubscriber()
                  .onPermissionGrantedAction { cachedEntityDelegate.startCaching(entity, cachedModelHelper.getPodcastPath(entity)) })
   }

   fun onDeletePodcastRequired(entity: CachedModel) {
      view.showDeleteDialog(entity)
   }

   fun onDeletePodcastAccepted(entity: CachedModel) {
      cachedEntityDelegate.deleteCache(entity, cachedModelHelper.getPodcastPath(entity))
   }

   fun onCancelPodcastRequired(entity: CachedModel) {
      view.onCancelDialog(entity)
   }

   fun onCancelPodcastAccepted(entity: CachedModel) {
      cachedEntityDelegate.cancelCaching(entity, cachedModelHelper.getPodcastPath(entity))
   }

   fun play(podcast: Podcast) {
      try {
         val entity = podcast.cacheEntity
         if (cachedModelHelper.isCachedPodcast(entity)) {
            activityRouter.openPodcastPlayer(cachedModelHelper.getPodcastPath(entity), podcast.title)
         } else {
            activityRouter.openPodcastPlayer(podcast.fileUrl, podcast.title)
         }
      } catch (e: ActivityNotFoundException) {
         view.informUser(R.string.audio_app_not_found_exception)
      }
   }

   fun track() {
      analyticsInteractor.analyticsActionPipe().send(ViewPodcastAnalyticsAction())
   }

   interface View : RxView {

      fun startLoading()

      fun finishLoading(noMoreElements: Boolean)

      fun setItems(@NotNull items: List<*>)

      fun notifyItemChanged(entity: CachedModel?)

      fun showDeleteDialog(entity: CachedModel)

      fun onCancelDialog(entity: CachedModel)
   }
}
