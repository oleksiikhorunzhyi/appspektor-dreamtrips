package com.worldventures.dreamtrips.social.ui.video.view.util


import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

import com.worldventures.core.ui.util.ViewUtils
import com.worldventures.core.ui.view.adapter.BaseArrayListAdapter
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader

class LayoutManagerProvider {

   fun forPresentation(context: Context, adapter: BaseArrayListAdapter<*>): RecyclerView.LayoutManager {
      val landscape = ViewUtils.isLandscapeOrientation(context)
      val tablet = ViewUtils.isTablet(context)
      val spanCount = if (landscape && tablet) 3 else if (landscape || tablet) 2 else 1
      val layoutManager = GridLayoutManager(context, spanCount)
      layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
         override fun getSpanSize(position: Int): Int {
            return if (adapter.getItem(position) is MediaHeader) spanCount else 1
         }
      }
      return layoutManager
   }

}
