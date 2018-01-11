package com.worldventures.dreamtrips.social.ui.video.view.util

import android.content.Context
import android.support.annotation.StringRes

import com.afollestad.materialdialogs.MaterialDialog

class VideoDialogHelper {

   fun showDialog(context: Context, @StringRes title: Int, @StringRes content: Int, @StringRes positive: Int,
                  @StringRes negative: Int, videoDialogClick: VideoDialogClickListener) {
      MaterialDialog.Builder(context).title(title)
            .content(content)
            .positiveText(positive)
            .negativeText(negative)
            .onPositive { _, _ -> videoDialogClick.onClick() }
            .onNegative { dialog, _ -> dialog.dismiss() }
            .show()
   }

   interface VideoDialogClickListener {
      fun onClick()
   }

}
