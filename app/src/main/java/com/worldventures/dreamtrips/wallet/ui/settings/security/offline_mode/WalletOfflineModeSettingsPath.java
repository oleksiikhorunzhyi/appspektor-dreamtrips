package com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_settings_offline_mode)
public class WalletOfflineModeSettingsPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
