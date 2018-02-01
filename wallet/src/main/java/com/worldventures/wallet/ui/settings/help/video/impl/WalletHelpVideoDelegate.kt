package com.worldventures.wallet.ui.settings.help.video.impl

import android.content.Context
import android.net.Uri
import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.core.utils.ProjectTextUtils.defaultIfEmpty
import com.worldventures.wallet.ui.settings.help.video.WalletHelpVideoScreen
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel
import java.io.File
import java.util.Locale

private const val MAX_PROGRESS = 100

class WalletHelpVideoDelegate(private val context: Context) {

   private var videoLocales: List<VideoLocale>? = null
   private var lastVideoLocale: VideoLocale? = null

   private val defaultLocaleName: String
      get() = Locale.getDefault().language + "-" + Locale.getDefault().country.toLowerCase(Locale.US)

   val lastSelectedLocaleIndex: Int
      get() = videoLocales?.indexOf(lastVideoLocale) ?: 0

   val defaultLanguageFromLastLocales: VideoLanguage
      get() = getDefaultLanguage(lastVideoLocale)

   fun getDefaultLanguage(videoLocales: List<VideoLocale>?): VideoLanguage {
      var videoLocale: VideoLocale? = null
      if (videoLocales != null && videoLocales.isNotEmpty()) {
         videoLocale = videoLocales.firstOrNull {
            it.country.equals(Locale.getDefault().country, ignoreCase = true)
         }
      }

      //for retry when HttpError
      if (videoLocale == null && lastVideoLocale != null) {
         videoLocale = lastVideoLocale
      }

      return getDefaultLanguage(videoLocale)
   }

   private fun getDefaultLanguage(videoLocale: VideoLocale?): VideoLanguage {
      if (videoLocale != null) {
         val videoLanguage = videoLocale.languages
               .firstOrNull { it.localeName.equals(Locale.getDefault().language, ignoreCase = true) }
         if (videoLanguage != null) {
            return videoLanguage
         }
      }
      return VideoLanguage(Locale.getDefault().displayLanguage, defaultLocaleName)
   }

   fun getPathForCache(entity: CachedModel): String = getFilePath(entity.url)

   fun obtainVideoLanguage(video: WalletVideoModel): String = defaultIfEmpty(video.video.language, "null")

   fun playVideo(video: WalletVideoModel): Uri {
      val videoEntity = video.video.cacheEntity
      var parse = Uri.parse(video.video.videoUrl)
      if (isCached(videoEntity)) {
         parse = Uri.parse(getFilePath(videoEntity.url))
      }
      return parse
   }

   fun isCurrentSelectedVideoLocale(videoLocale: VideoLocale) = lastVideoLocale == null || videoLocale == lastVideoLocale

   fun processCachingState(cachedEntity: CachedModel, view: WalletHelpVideoScreen) {
      view.currentItems
            .filter { it != null }
            .map { it.video }
            .filter({ video -> video.cacheEntity.uuid == cachedEntity.uuid })
            .forEach({ video ->
               video.cacheEntity = cachedEntity
               view.notifyItemChanged(cachedEntity)
            })
   }

   fun getDefaultLocaleIndex(videoLocales: List<VideoLocale>): Int {
      val videoLocale = videoLocales.firstOrNull {
         it.country.equals(Locale.getDefault().country, ignoreCase = true)
      }
      return videoLocale?.let { videoLocales.indexOf(it) } ?: 0
   }

   fun setVideoLocales(videoLocales: List<VideoLocale>) {
      this.videoLocales = videoLocales
   }

   fun setCurrentSelectedVideoLocale(videoLocale: VideoLocale) {
      this.lastVideoLocale = videoLocale
   }

   private fun isCached(cachedModel: CachedModel): Boolean =
         File(getFilePath(cachedModel.url)).exists() && cachedModel.progress == MAX_PROGRESS

   private fun getFilePath(url: String): String = context.filesDir.path + File.separator + getFileName(url)

   private fun getFileName(url: String): String = url.substring(url.lastIndexOf('/') + 1)
}
