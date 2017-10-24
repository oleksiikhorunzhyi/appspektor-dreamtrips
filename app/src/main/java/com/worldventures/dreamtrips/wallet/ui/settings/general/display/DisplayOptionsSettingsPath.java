package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

@Layout(R.layout.screen_wallet_settings_display_options)
public class DisplayOptionsSettingsPath extends StyledPath {

   @Nullable
   private SmartCardUser smartCardUser;
   private final DisplayOptionsSource displayOptionsSource;

   public DisplayOptionsSettingsPath(DisplayOptionsSource displayOptionsSource) {
      this.displayOptionsSource = displayOptionsSource;
   }

   public DisplayOptionsSettingsPath(DisplayOptionsSource displayOptionsSource, @Nullable SmartCardUser smartCardUser) {
      this.displayOptionsSource = displayOptionsSource;
      this.smartCardUser = smartCardUser;
   }

   @Nullable
   SmartCardUser smartCardUser() {
      return smartCardUser;
   }

   DisplayOptionsSource displayOptionsSource() {
      return displayOptionsSource;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}