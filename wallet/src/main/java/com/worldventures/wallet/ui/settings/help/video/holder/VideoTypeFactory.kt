package com.worldventures.wallet.ui.settings.help.video.holder

import com.worldventures.wallet.ui.common.adapter.HolderTypeFactory
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel

interface VideoTypeFactory : HolderTypeFactory {

   fun type(video: WalletVideoModel): Int
}
