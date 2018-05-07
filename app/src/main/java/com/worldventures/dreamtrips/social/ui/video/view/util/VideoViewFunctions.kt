package com.worldventures.dreamtrips.social.ui.video.view.util

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog

import com.worldventures.core.ui.util.ViewUtils
import com.worldventures.core.ui.view.adapter.BaseArrayListAdapter
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader

const val TABLET_LANDSCAPE_SPAN_COUNT = 3
const val PHONE_LANDSCAPE_OR_TABLET_PORTRAIT_SPAM_COUNT = 2
const val DEFAULT_COLUMN_SPAN = 1

fun provideGridLayoutManager(context: Context, adapter: BaseArrayListAdapter<*>): RecyclerView.LayoutManager {
   val landscape = ViewUtils.isLandscapeOrientation(context)
   val tablet = ViewUtils.isTablet(context)
   val spanCount = when {
      landscape && tablet -> TABLET_LANDSCAPE_SPAN_COUNT
      landscape || tablet -> PHONE_LANDSCAPE_OR_TABLET_PORTRAIT_SPAM_COUNT
      else -> DEFAULT_COLUMN_SPAN
   }
   val layoutManager = GridLayoutManager(context, spanCount)
   layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
         return if (adapter.getItem(position) is MediaHeader) spanCount else 1
      }
   }
   return layoutManager
}

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
