package com.worldventures.dreamtrips.social.ui.reptools.delegate

import android.content.Context
import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.core.modules.video.service.storage.MediaModelStorage
import java.util.Locale

@Suppress("DEPRECATION")
open class LocaleVideoDelegate(val mediaModelStorage: MediaModelStorage) {

   private var videoLocales: List<VideoLocale>? = null

   open fun fetchLocaleAndLanguage(context: Context, locales: List<VideoLocale>?): Pair<VideoLocale, VideoLanguage> {
      var videoLocale = mediaModelStorage.lastSelectedVideoLocale
      var videoLanguage = mediaModelStorage.lastSelectedVideoLanguage

      if (videoLocale != null && videoLanguage != null || locales == null) return Pair(videoLocale, videoLanguage)

      videoLocale = getCurrentLocale(locales, context.resources.configuration.locale)
      if (videoLocale == null) videoLocale = getCurrentLocale(locales, Locale.US)
      if (videoLocale != null) videoLanguage = getCurrentLanguage(context, videoLocale.languages)

      return Pair(videoLocale, videoLanguage)
   }

   private fun getCurrentLocale(locales: List<VideoLocale>, locale: Locale): VideoLocale? {
      return locales.firstOrNull { it.country.equals(locale.country, ignoreCase = true) }
   }

   private fun getCurrentLanguage(context: Context, languages: List<VideoLanguage>): VideoLanguage {
      val currentLocale = context.resources.configuration.locale
      val localName = String.format("%s-%s", currentLocale.language, currentLocale.country).toLowerCase()
      val videoLanguage = languages.firstOrNull { it.localeName.equals(localName, ignoreCase = true) }

      return videoLanguage ?: languages[0]
   }

   open fun saveVideoLocaleAndLanguage(videoLocale: VideoLocale, videoLanguage: VideoLanguage) {
      mediaModelStorage.saveLastSelectedVideoLocale(videoLocale)
      mediaModelStorage.saveLastSelectedVideoLanguage(videoLanguage)
   }

   open fun saveVideoLocaleList(locales: List<VideoLocale>) {
      videoLocales = ArrayList<VideoLocale>(locales)
   }

   open fun fetchVideoLocaleList() = if (videoLocales != null) ArrayList<VideoLocale>(videoLocales) else ArrayList()

}