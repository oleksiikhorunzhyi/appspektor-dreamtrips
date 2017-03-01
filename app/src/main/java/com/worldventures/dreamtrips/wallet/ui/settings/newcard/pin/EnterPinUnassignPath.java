package com.worldventures.dreamtrips.wallet.ui.settings.newcard.pin;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_enter_pin_for_new_card)
public class EnterPinUnassignPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
