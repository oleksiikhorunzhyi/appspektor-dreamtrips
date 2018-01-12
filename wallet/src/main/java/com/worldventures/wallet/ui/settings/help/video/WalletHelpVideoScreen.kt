package com.worldventures.wallet.ui.settings.help.video

import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand
import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel

import java.util.ArrayList

import io.techery.janet.operationsubscriber.view.OperationView

interface WalletHelpVideoScreen : WalletScreen {

   val currentItems: List<WalletVideoModel>

   fun setVideos(videos: ArrayList<WalletVideoModel>)

   fun setVideoLocales(videoLocales: ArrayList<VideoLocale>)

   fun provideOperationLoadVideos(): OperationView<GetMemberVideosCommand>

   fun provideOperationLoadLanguages(): OperationView<GetVideoLocalesCommand>

   fun confirmCancelDownload(entity: CachedModel)

   fun confirmDeleteVideo(entity: CachedModel)

   fun notifyItemChanged(cachedEntity: CachedModel)

   fun showDialogChosenLanguage(videoLocale: VideoLocale)

   fun setSelectedLocale(index: Int)

   fun showRefreshing(show: Boolean)
}
