package com.worldventures.wallet.ui.settings.help.video

import com.worldventures.core.model.CachedModel
import com.worldventures.wallet.ui.common.base.WalletPresenter
import com.worldventures.wallet.ui.settings.help.video.impl.HelpVideoLocale
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel

interface WalletHelpVideoPresenter : WalletPresenter<WalletHelpVideoScreen> {

   fun goBack()

   fun fetchLocales()

   fun fetchVideos(videoLocales: HelpVideoLocale)

   fun downloadVideo(entity: CachedModel)

   fun deleteCachedVideo(entity: CachedModel)

   fun cancelCachingVideo(entity: CachedModel)

   fun onPlayVideo(entity: WalletVideoModel)

   fun onCancelAction(entity: CachedModel)

   fun onDeleteAction(entity: CachedModel)
}
