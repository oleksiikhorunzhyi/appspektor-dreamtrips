package com.worldventures.dreamtrips.wallet.ui.settings.security.removecards;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_settings_clear_cards)
public class WalletAutoClearCardsPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
