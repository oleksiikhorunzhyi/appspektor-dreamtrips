package com.worldventures.wallet.ui.settings.help.video.holder

import com.worldventures.core.ui.view.custom.PinProgressButton
import com.worldventures.wallet.ui.settings.help.video.delegate.WalletVideoCallback
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel

abstract class WalletVideoHolderDelegate {

   abstract fun sendPlayVideoAction(video: WalletVideoModel)

   abstract fun sendMembershipVideoAction(video: WalletVideoModel)

   abstract fun createHelper(progressView: PinProgressButton): WalletVideoHolderHelper

   interface WalletVideoHolderHelper {

      fun onDownloadClick(videoCallback: WalletVideoCallback, walletVideoModel: WalletVideoModel)

      fun setModelObject(walletVideoModel: WalletVideoModel)

      fun syncUIStateWithModel()
   }
}
