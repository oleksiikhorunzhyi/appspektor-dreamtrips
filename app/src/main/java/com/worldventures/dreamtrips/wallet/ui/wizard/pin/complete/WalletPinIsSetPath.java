package com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

@Layout(R.layout.screen_wallet_wizard_pin_is_set)
public class WalletPinIsSetPath extends StyledPath {

   public final SmartCard smartCard;

   public WalletPinIsSetPath(SmartCard smartCard) {
      this.smartCard = smartCard;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
