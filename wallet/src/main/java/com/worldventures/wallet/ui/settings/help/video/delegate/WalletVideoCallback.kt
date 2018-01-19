package com.worldventures.wallet.ui.settings.help.video.delegate

import com.worldventures.core.modules.video.cell.ProgressMediaButtonActions
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel

interface WalletVideoCallback : ProgressMediaButtonActions<WalletVideoModel> {

   fun onPlayVideoClicked(entity: WalletVideoModel)
}
