package com.worldventures.dreamtrips.wallet.ui.wizard.pin.success;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;

@Layout(R.layout.screen_wallet_wizard_success)
public class PinSetSuccessPath extends StyledPath {

   public final Action action;

   public PinSetSuccessPath(Action action) {
      this.action = action;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
