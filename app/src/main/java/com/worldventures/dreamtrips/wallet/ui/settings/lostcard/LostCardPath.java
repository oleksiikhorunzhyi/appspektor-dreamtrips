package com.worldventures.dreamtrips.wallet.ui.settings.lostcard;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_settings_lost_card)
public class LostCardPath extends StyledPath {
   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
