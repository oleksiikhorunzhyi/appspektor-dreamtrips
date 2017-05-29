package com.worldventures.dreamtrips.wallet.ui.settings.help.video;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.wallet_settings_help_video)
public class WalletHelpVideoPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
