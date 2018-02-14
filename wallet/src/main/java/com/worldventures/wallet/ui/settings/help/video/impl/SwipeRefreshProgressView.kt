package com.worldventures.wallet.ui.settings.help.video.impl

import android.support.v4.widget.SwipeRefreshLayout
import io.techery.janet.operationsubscriber.view.ProgressView

class SwipeRefreshProgressView<T>(private val refreshLayout: SwipeRefreshLayout) : ProgressView<T> {

   override fun isProgressVisible() = refreshLayout.isRefreshing

   override fun onProgressChanged(p0: Int) {
//      nothing
   }

   override fun hideProgress() {
      refreshLayout.isRefreshing = false
   }

   override fun showProgress(p0: T) {
      refreshLayout.isRefreshing = true
   }
}
