package com.worldventures.wallet.ui.settings.help.video

import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand
import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import com.worldventures.wallet.ui.settings.help.video.impl.HelpVideoLocale
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel
import io.techery.janet.operationsubscriber.view.OperationView
import java.util.ArrayList

interface WalletHelpVideoScreen : WalletScreen {

   var videos: ArrayList<WalletVideoModel>

   fun setVideoLocales(videoLocales: ArrayList<VideoLocale>, defaultLocale: HelpVideoLocale)

   fun provideOperationLoadVideos(): OperationView<GetMemberVideosCommand>

   fun provideOperationLoadLanguages(): OperationView<GetVideoLocalesCommand>

   fun confirmCancelDownload(entity: CachedModel)

   fun confirmDeleteVideo(entity: CachedModel)

   fun notifyItemChanged(cachedEntity: CachedModel)
}
