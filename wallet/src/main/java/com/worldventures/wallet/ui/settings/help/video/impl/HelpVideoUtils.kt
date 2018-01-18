package com.worldventures.wallet.ui.settings.help.video.impl

import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.core.modules.video.model.VideoLocale
import java.util.Locale

internal fun obtainDefaultLanguage(videoLocales: List<VideoLocale>): HelpVideoLocale {
   val videoLocale: VideoLocale? = videoLocales.firstOrNull {
      it.country.equals(Locale.getDefault().country, ignoreCase = true)
   } ?: videoLocales.getOrNull(0)
   return HelpVideoLocale(videoLocale, obtainDefaultLanguage(videoLocale))
}

internal fun obtainDefaultLanguage(videoLocale: VideoLocale?): VideoLanguage {
   return videoLocale?.languages?.firstOrNull {
      it.localeName.equals(Locale.getDefault().language, ignoreCase = true)
   } ?: VideoLanguage(Locale.getDefault().displayLanguage, createDefaultLocaleName())
}

private fun createDefaultLocaleName() =
      "${Locale.getDefault().language}-${Locale.getDefault().country.toLowerCase(Locale.US)}"

