package com.worldventures.wallet.ui.settings.help.video.holder

import android.net.Uri

import com.worldventures.wallet.databinding.ItemWalletVideoBinding
import com.worldventures.wallet.ui.common.adapter.BaseHolder
import com.worldventures.wallet.ui.settings.help.video.delegate.WalletVideoCallback
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel

class WalletVideoHolder(private val binding: ItemWalletVideoBinding,
                        private val videoActionsCallback: WalletVideoCallback,
                        private val videoHolderDelegate: WalletVideoHolderDelegate) : BaseHolder<WalletVideoModel>(binding.root) {

   private val videoHolderHelper = videoHolderDelegate.createHelper(binding.downloadProgress)

   override fun setData(videoModel: WalletVideoModel) {
      val video = videoModel.video
      binding.ivBg.setImageURI(Uri.parse(video.imageUrl), null)
      binding.tvTitle.text = video.videoName

      videoHolderHelper.setModelObject(videoModel)
      videoHolderHelper.syncUIStateWithModel()

      binding.ivPlay.setOnClickListener {
         videoHolderDelegate.sendPlayVideoAction(videoModel)
         videoActionsCallback.onPlayVideoClicked(videoModel)
      }

      binding.downloadProgress.setOnClickListener {
         videoHolderHelper.onDownloadClick(videoActionsCallback, videoModel)
         videoHolderDelegate.sendMembershipVideoAction(videoModel)
      }
   }
}
