package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

@Layout(R.layout.screen_wallet_settings_display_options)
public class DisplayOptionsSettingsPath extends StyledPath {

   private final SmartCardUser smartCardUser;
   private final DisplayOptionsSource displayOptionsSource;

   public DisplayOptionsSettingsPath(SmartCardUser smartCardUser, DisplayOptionsSource displayOptionsSource) {
      this.smartCardUser = smartCardUser;
      this.displayOptionsSource = displayOptionsSource;
   }

   public SmartCardUser smartCardUser() {
      return smartCardUser;
   }

   public DisplayOptionsSource displayOptionsSource() {
      return displayOptionsSource;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}