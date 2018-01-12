package com.worldventures.wallet.ui.settings.help.video

import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.wallet.ui.common.base.WalletPresenter
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel

interface WalletHelpVideoPresenter : WalletPresenter<WalletHelpVideoScreen> {

   fun goBack()

   fun fetchVideoAndLocales()

   fun refreshVideos()

   fun fetchVideos(videoLanguage: VideoLanguage?)

   fun onSelectedLocale(item: VideoLocale)

   fun downloadVideo(entity: CachedModel)

   fun deleteCachedVideo(entity: CachedModel)

   fun cancelCachingVideo(entity: CachedModel)

   fun onPlayVideo(entity: WalletVideoModel)

   fun onCancelAction(entity: CachedModel)

   fun onDeleteAction(entity: CachedModel)

   fun onSelectLastLocale()

}
