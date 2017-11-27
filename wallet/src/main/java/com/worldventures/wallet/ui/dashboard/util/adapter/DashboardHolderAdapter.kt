package com.worldventures.wallet.ui.dashboard.util.adapter

import android.os.Handler
import android.os.Looper
import android.support.v7.util.DiffUtil
import com.worldventures.wallet.ui.common.adapter.BaseViewModel
import com.worldventures.wallet.ui.common.adapter.HolderTypeFactory
import com.worldventures.wallet.ui.common.adapter.MultiHolderAdapter
import java.util.concurrent.Executors

class DashboardHolderAdapter<ITEM : BaseViewModel<*>>(items: List<ITEM>, holderTypeFactory: HolderTypeFactory)
   : MultiHolderAdapter<ITEM>(items, holderTypeFactory) {

   private val exService = Executors.newSingleThreadExecutor()
   private val handler = Handler(Looper.getMainLooper())

   fun swapList(newList: List<ITEM>) {
      exService.execute {
         val diffResult = DiffUtil.calculateDiff(CardDiffCallBack(items, newList))
         handler.post {
            diffResult.dispatchUpdatesTo(this)
            items = newList
         }
      }
   }
}
