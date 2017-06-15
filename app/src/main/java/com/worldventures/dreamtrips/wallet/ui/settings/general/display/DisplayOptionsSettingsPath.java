package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_settings_display_options)
public class DisplayOptionsSettingsPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}