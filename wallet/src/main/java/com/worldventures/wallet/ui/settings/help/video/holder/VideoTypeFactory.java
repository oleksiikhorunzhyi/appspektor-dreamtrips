package com.worldventures.wallet.ui.settings.help.video.holder;

import com.worldventures.wallet.ui.common.adapter.HolderTypeFactory;
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel;

public interface VideoTypeFactory extends HolderTypeFactory {

   int type(WalletVideoModel video);
}
