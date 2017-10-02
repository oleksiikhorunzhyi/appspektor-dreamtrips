package com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder;

import com.worldventures.core.ui.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate.WalletVideoCallback;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

public abstract class WalletVideoHolderDelegate {

   public abstract void sendPlayVideoAction(WalletVideoModel video);

   public abstract void sendMembershipVideoAction(WalletVideoModel video);

   protected abstract WalletVideoHolderHelper createHelper(PinProgressButton progressView);

   protected interface WalletVideoHolderHelper {

      void onDownloadClick(WalletVideoCallback videoCallback, WalletVideoModel walletVideoModel);

      void setModelObject(WalletVideoModel walletVideoModel);

      void syncUIStateWithModel();
   }
}
