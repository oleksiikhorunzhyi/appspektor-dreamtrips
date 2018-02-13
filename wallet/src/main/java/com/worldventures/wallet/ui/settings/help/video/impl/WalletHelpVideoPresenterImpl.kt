package com.worldventures.wallet.ui.settings.help.video.impl

import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.modules.video.model.VideoCategory
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.core.modules.video.service.MemberVideosInteractor
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand
import com.worldventures.core.modules.video.service.command.UpdateStatusCachedEntityCommand
import com.worldventures.core.service.CachedEntityDelegate
import com.worldventures.core.service.CachedEntityInteractor
import com.worldventures.core.service.command.CachedEntityCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.video.WalletHelpVideoPresenter
import com.worldventures.wallet.ui.settings.help.video.WalletHelpVideoScreen
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel
import io.techery.janet.ActionState
import io.techery.janet.helper.ActionStateSubscriber
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.ArrayList

class WalletHelpVideoPresenterImpl(navigator: Navigator, deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                   private val memberVideosInteractor: MemberVideosInteractor,
                                   private val cachedEntityInteractor: CachedEntityInteractor,
                                   private val cachedEntityDelegate: CachedEntityDelegate,
                                   private val helpVideoDelegate: WalletHelpVideoDelegate)
   : WalletPresenterImpl<WalletHelpVideoScreen>(navigator, deviceConnectionDelegate), WalletHelpVideoPresenter {

   override fun attachView(view: WalletHelpVideoScreen) {
      super.attachView(view)

      observeUpdateStatusCachedEntity()
      subscribeToCachingStatusUpdates()
      observeFetchVideoLocales()
   }

   private fun observeUpdateStatusCachedEntity() {
      cachedEntityInteractor.updateStatusCachedEntityCommandPipe()
            .observe()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ActionStateSubscriber<UpdateStatusCachedEntityCommand>()
                  .onSuccess { handleUpdatedStatusCachedEntities(it.result) }
            )
   }

   private fun handleUpdatedStatusCachedEntities(categories: List<VideoCategory>) {
      view.videos = convert(categories[0].videos)
   }

   private fun convert(videos: List<Video>): ArrayList<WalletVideoModel> =
         if (videos.isNotEmpty()) ArrayList(videos.map { WalletVideoModel(it) }.toList()) else ArrayList()

   override fun fetchLocales() {
      memberVideosInteractor.videoLocalesPipe.send(GetVideoLocalesCommand())
   }

   private fun observeFetchVideoLocales() {
      memberVideosInteractor.videoLocalesPipe.observe()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationLoadLanguages())
                  .onSuccess({ this.handleLoadedLocales(it.result) })
                  .create())
   }

   override fun fetchVideos(videoLocales: HelpVideoLocale) {
      memberVideosInteractor.memberVideosPipe
            .createObservable(GetMemberVideosCommand.forHelpSmartCardVideos(videoLocales.videoLanguage))
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationLoadVideos())
                  .onSuccess { onVideoLoaded(it.result) }
                  .create())
   }

   private fun handleLoadedLocales(videoLocales: List<VideoLocale>) {
      view.setVideoLocales(ArrayList(videoLocales), obtainDefaultLanguage(videoLocales))
   }

   private fun onVideoLoaded(categories: List<VideoCategory>) {
      cachedEntityInteractor.updateStatusCachedEntityCommandPipe()
            .send(UpdateStatusCachedEntityCommand(categories))
   }

   override fun goBack() {
      navigator.goBack()
   }

   override fun onPlayVideo(entity: WalletVideoModel) {
      val videoUri = helpVideoDelegate.obtainVideoUri(entity)
      navigator.goVideoPlayer(videoUri, entity.video.uid, entity.video.videoName, javaClass, entity.video.language)
   }

   private fun subscribeToCachingStatusUpdates() {
      Observable.merge<ActionState<out CachedEntityCommand>>(
            cachedEntityInteractor.downloadCachedModelPipe.observe(),
            cachedEntityInteractor.deleteCachedModelPipe.observe())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .map<CachedModel> { actionState -> actionState.action.cachedModel }
            .subscribe { entity -> helpVideoDelegate.processCachingState(entity, view) }
   }

   override fun cancelCachingVideo(entity: CachedModel) {
      view.confirmCancelDownload(entity)
   }

   override fun onCancelAction(entity: CachedModel) {
      cachedEntityDelegate.cancelCaching(entity, helpVideoDelegate.getPathForCache(entity))
   }

   override fun deleteCachedVideo(entity: CachedModel) {
      view.confirmDeleteVideo(entity)
   }

   override fun onDeleteAction(entity: CachedModel) {
      cachedEntityDelegate.deleteCache(entity, helpVideoDelegate.getPathForCache(entity))
   }

   override fun downloadVideo(entity: CachedModel) {
      cachedEntityDelegate.startCaching(entity, helpVideoDelegate.getPathForCache(entity))
   }
}
