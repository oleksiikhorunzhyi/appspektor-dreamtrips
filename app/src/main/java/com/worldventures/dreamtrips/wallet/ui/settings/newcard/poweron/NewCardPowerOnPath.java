package com.worldventures.dreamtrips.wallet.ui.settings.newcard.poweron;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_new_card_power_on)
public class NewCardPowerOnPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
