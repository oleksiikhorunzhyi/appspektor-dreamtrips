package com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder;

import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletVideo;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.HolderTypeFactory;

public interface VideoTypeFactory extends HolderTypeFactory {

   int type(WalletVideo video);
}
