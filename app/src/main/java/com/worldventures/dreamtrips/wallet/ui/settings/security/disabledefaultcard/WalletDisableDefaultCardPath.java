package com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_settings_disable_default_card)
public class WalletDisableDefaultCardPath extends StyledPath {

   public WalletDisableDefaultCardPath() {
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
