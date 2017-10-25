package com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate;

import com.worldventures.core.modules.video.cell.ProgressVideoButtonActions;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

public interface WalletVideoCallback extends ProgressVideoButtonActions<WalletVideoModel> {

   void onPlayVideoClicked(WalletVideoModel entity);
}
