package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_provisioning_blocked)
public class WalletProvisioningBlockedPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
