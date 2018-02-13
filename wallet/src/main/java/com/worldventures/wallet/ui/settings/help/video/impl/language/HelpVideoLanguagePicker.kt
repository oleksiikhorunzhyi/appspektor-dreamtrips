package com.worldventures.wallet.ui.settings.help.video.impl.language

import android.content.Context
import android.view.View
import android.widget.ListPopupWindow
import com.afollestad.materialdialogs.MaterialDialog
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.core.ui.util.ViewUtils
import com.worldventures.wallet.R
import com.worldventures.wallet.ui.settings.help.video.impl.HelpVideoLocale
import com.worldventures.wallet.ui.settings.help.video.impl.language.adapter.VideoLanguagesAdapter
import com.worldventures.wallet.ui.settings.help.video.impl.language.adapter.VideoLocaleAdapter

private typealias LocaleCallback = (HelpVideoLocale) -> Unit

private const val POPUP_WINDOW_WIDTH_DP = 200f

class HelpVideoLanguagePicker(
      private val context: Context,
      private val callback: LocaleCallback) {

   private var videoLocales: List<VideoLocale>? = null

   private fun videoLanguageSelected(videoLocale: VideoLocale) {
      MaterialDialog.Builder(context)
            .title(R.string.wallet_settings_help_video_language_dialog_title)
            .adapter(VideoLanguagesAdapter(context, videoLocale.languages)
            ) { dialog, _, which, _ ->
               callback.invoke(HelpVideoLocale(
                     videoLocale = videoLocale,
                     videoLanguage = videoLocale.languages[which])
               )
               dialog.dismiss()
            }
            .build()
            .show()
   }

   fun showLocalePicker(anchor: View) {
      val locales = videoLocales ?: return
      val popup = ListPopupWindow(context)
      popup.setAdapter(VideoLocaleAdapter(context, locales))
      popup.setOnItemClickListener { _, _, position, _ ->
         videoLanguageSelected(locales[position])
         popup.dismiss()
      }

      popup.width = ViewUtils.pxFromDp(anchor.context, POPUP_WINDOW_WIDTH_DP).toInt()
      popup.isModal = true
      popup.anchorView = anchor
      popup.show()
   }

   fun addLocales(videoLocales: List<VideoLocale>) {
      this.videoLocales = videoLocales
   }
}
