package com.worldventures.wallet.ui.settings.help.video.holder

import android.databinding.DataBindingUtil.inflate
import android.view.LayoutInflater.from
import android.view.ViewGroup
import com.worldventures.wallet.R
import com.worldventures.wallet.ui.common.adapter.BaseHolder
import com.worldventures.wallet.ui.settings.help.video.delegate.WalletVideoCallback
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel

class VideoHolderFactoryImpl(private val videoActionsCallback: WalletVideoCallback,
                             private val videoHolderDelegate: WalletVideoHolderDelegate) : VideoTypeFactory {

   override fun type(video: WalletVideoModel) = R.layout.item_wallet_video

   override fun holder(parent: ViewGroup, viewType: Int): BaseHolder<*> {
      return when (viewType) {
         R.layout.item_wallet_video ->
            WalletVideoHolder(inflate(from(parent.context), viewType, parent, false), videoActionsCallback, videoHolderDelegate)
         else -> throw IllegalArgumentException()
      }
   }
}
