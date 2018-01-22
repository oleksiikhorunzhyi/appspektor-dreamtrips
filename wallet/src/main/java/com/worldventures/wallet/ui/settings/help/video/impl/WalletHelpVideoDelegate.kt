package com.worldventures.wallet.ui.settings.help.video.impl

import android.content.Context
import android.net.Uri
import com.worldventures.core.model.CachedModel
import com.worldventures.wallet.ui.settings.help.video.WalletHelpVideoScreen
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel
import java.io.File

private const val MAX_PROGRESS = 100

class WalletHelpVideoDelegate(private val context: Context) {

   fun getPathForCache(entity: CachedModel): String = getFilePath(entity.url)

   fun obtainVideoUri(video: WalletVideoModel): Uri {
      val videoEntity = video.video.cacheEntity
      var parse = Uri.parse(video.video.videoUrl)
      if (isCached(videoEntity)) {
         parse = Uri.parse(getFilePath(videoEntity.url))
      }
      return parse
   }

   fun processCachingState(cachedEntity: CachedModel, view: WalletHelpVideoScreen) {
      view.videos
            .map { it.video }
            .filter({ video -> video.cacheEntity.uuid == cachedEntity.uuid })
            .forEach({ video ->
               video.cacheEntity = cachedEntity
               view.notifyItemChanged(cachedEntity)
            })
   }

   private fun isCached(cachedModel: CachedModel): Boolean =
         File(getFilePath(cachedModel.url)).exists() && cachedModel.progress == MAX_PROGRESS

   private fun getFilePath(url: String): String = context.filesDir.path + File.separator + getFileName(url)

   private fun getFileName(url: String): String = url.substring(url.lastIndexOf('/') + 1)
}
