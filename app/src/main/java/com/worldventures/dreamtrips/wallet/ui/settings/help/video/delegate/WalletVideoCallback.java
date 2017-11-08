package com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate;

import com.worldventures.core.modules.video.cell.ProgressMediaButtonActions;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

public interface WalletVideoCallback extends ProgressMediaButtonActions<WalletVideoModel> {

   void onPlayVideoClicked(WalletVideoModel entity);
}
