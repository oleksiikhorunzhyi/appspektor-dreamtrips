package com.worldventures.dreamtrips.wallet.ui.settings.help;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_settings_help)
public class WalletHelpSettingsPath extends StyledPath {

   public WalletHelpSettingsPath() {
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

   @Override
   public boolean equals(Object o) {
      // for back navigation
      return o != null && getClass().equals(o.getClass());
   }
}
