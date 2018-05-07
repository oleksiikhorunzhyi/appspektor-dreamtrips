package com.worldventures.dreamtrips.social.ui.video.presenter

import android.net.Uri
import android.text.TextUtils
import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.modules.video.utils.CachedModelHelper
import com.worldventures.core.service.CachedEntityDelegate
import com.worldventures.core.service.CachedEntityInteractor
import com.worldventures.core.service.command.CachedEntityCommand
import com.worldventures.core.ui.util.permission.PermissionConstants
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.core.ui.util.permission.PermissionSubscriber
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import rx.Observable
import javax.inject.Inject

private const val ABSENT_VIDEO_LANGUAGE = "null"

abstract class VideoBasePresenter<V : VideoBasePresenter.View> : Presenter<V>() {

   @Inject lateinit var cachedEntityInteractor: CachedEntityInteractor
   @Inject lateinit var cachedEntityDelegate: CachedEntityDelegate
   @Inject lateinit var cachedModelHelper: CachedModelHelper
   @Inject lateinit var permissionDispatcher: PermissionDispatcher

   override fun takeView(view: V) {
      super.takeView(view)
      subscribeToCachingStatusUpdates()
   }

   abstract fun reload()

   open fun subscribeToCachingStatusUpdates() {
      Observable.merge(
            cachedEntityInteractor.downloadCachedModelPipe.observe(),
            cachedEntityInteractor.deleteCachedModelPipe.observe())
            .compose(bindViewToMainComposer())
            .map { (it.action as? CachedEntityCommand)?.cachedModel?.uuid }
            .subscribe { it?.let { view.notifyItemChanged(it) } }
   }

   open fun onPlayVideo(video: Video) {
      val videoEntity = video.cacheEntity
      var parse = Uri.parse(video.videoUrl)
      if (cachedModelHelper.isCached(videoEntity)) {
         parse = Uri.parse(cachedModelHelper.getFilePath(videoEntity.url))
      }

      view.openPlayer(parse, video.videoName, obtainVideoLanguage(video))
   }

   fun downloadVideoRequired(video: Video) {
      permissionDispatcher.requestPermission(PermissionConstants.WRITE_EXTERNAL_STORAGE, false)
            .compose(bindView())
            .subscribe(PermissionSubscriber().onPermissionGrantedAction { downloadVideoAccepted(video) })
   }

   protected open fun downloadVideoAccepted(video: Video) {
      cachedEntityDelegate.startCaching(video.cacheEntity, getPathForCache(video.cacheEntity))
   }

   fun deleteCacheRequired(video: Video) = view.onDeleteAction(video.cacheEntity)

   fun deleteAccepted(entity: CachedModel) = cachedEntityDelegate.deleteCache(entity, getPathForCache(entity))

   fun cancelCachingRequired(video: Video) = view.onCancelCaching(video.cacheEntity)

   open fun cancelCachingAccepted(entity: CachedModel) = cachedEntityDelegate.cancelCaching(entity, getPathForCache(entity))

   open fun obtainVideoLanguage(video: Video) = if (!TextUtils.isEmpty(video.language)) video.language else ABSENT_VIDEO_LANGUAGE

   private fun getPathForCache(entity: CachedModel) = cachedModelHelper.getFilePath(entity.url)

   interface View : Presenter.View {
      fun startLoading()

      fun finishLoading()

      fun notifyItemChanged(uuid: String)

      fun onDeleteAction(entity: CachedModel)

      fun onCancelCaching(entity: CachedModel)

      fun openPlayer(url: Uri, videoName: String, language: String)
   }
}
