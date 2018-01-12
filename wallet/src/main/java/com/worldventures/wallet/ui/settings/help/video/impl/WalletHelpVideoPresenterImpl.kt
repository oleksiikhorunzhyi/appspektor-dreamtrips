package com.worldventures.wallet.ui.settings.help.video.impl

import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.modules.video.model.VideoCategory
import com.worldventures.core.modules.video.model.VideoLanguage
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

import java.util.ArrayList

import io.techery.janet.helper.ActionStateSubscriber
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

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
   }

   private fun observeUpdateStatusCachedEntity() {
      cachedEntityInteractor.updateStatusCachedEntityCommandPipe()
            .observe()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ActionStateSubscriber<UpdateStatusCachedEntityCommand>()
                  .onSuccess { handleUpdatedStatusCachedEntities(it.result) }
                  .onFail { _, _ -> view.showRefreshing(false) }
            )
   }

   private fun handleUpdatedStatusCachedEntities(categories: List<VideoCategory>) {
      view.setVideos(convert(categories[0].videos))
      view.showRefreshing(false)
   }

   private fun convert(videos: List<Video>): ArrayList<WalletVideoModel> =
         if (videos.isNotEmpty()) ArrayList(videos.map { WalletVideoModel(it) }.toList()) else ArrayList()

   override fun fetchVideoAndLocales() {
      fetchVideoLocales()
   }

   private fun fetchVideoLocales() {
      memberVideosInteractor.videoLocalesPipe
            .createObservable(GetVideoLocalesCommand())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationLoadLanguages())
                  .onSuccess({ this.handleLoadedLocales(it) })
                  .create())
   }

   override fun fetchVideos(videoLanguage: VideoLanguage?) {
      memberVideosInteractor.memberVideosPipe
            .createObservable(GetMemberVideosCommand.forHelpSmartCardVideos(videoLanguage))
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationLoadVideos())
                  .onStart { view.showRefreshing(true) }
                  .onSuccess { onVideoLoaded(it.result) }
                  .onFail { _, _ -> view.showRefreshing(false) }
                  .create())
   }

   private fun handleLoadedLocales(command: GetVideoLocalesCommand) {
      val locales = command.result
      helpVideoDelegate.setVideoLocales(locales)
      view.setVideoLocales(ArrayList(locales))
      view.setSelectedLocale(helpVideoDelegate.getDefaultLocaleIndex(locales))

      fetchVideos(helpVideoDelegate.getDefaultLanguage(locales))
   }

   private fun onVideoLoaded(categories: List<VideoCategory>) {
      cachedEntityInteractor.updateStatusCachedEntityCommandPipe()
            .send(UpdateStatusCachedEntityCommand(categories))
   }

   override fun goBack() {
      navigator.goBack()
   }

   override fun onPlayVideo(entity: WalletVideoModel) {
      val videoUri = helpVideoDelegate.playVideo(entity)
      navigator.goVideoPlayer(videoUri, entity.video.videoName,
            javaClass, helpVideoDelegate.obtainVideoLanguage(entity))
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

   override fun onSelectedLocale(item: VideoLocale) {
      if (!helpVideoDelegate.isCurrentSelectedVideoLocale(item)) {
         view.showDialogChosenLanguage(item)
      } else {
         helpVideoDelegate.setCurrentSelectedVideoLocale(item)
      }
   }

   override fun refreshVideos() {
      fetchVideos(helpVideoDelegate.defaultLanguageFromLastLocales)
   }

   override fun onSelectLastLocale() {
      view.setSelectedLocale(helpVideoDelegate.lastSelectedLocaleIndex)
   }
}
