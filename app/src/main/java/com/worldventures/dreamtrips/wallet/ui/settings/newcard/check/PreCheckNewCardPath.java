package com.worldventures.dreamtrips.wallet.ui.settings.newcard.check;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_pre_check_new_card)
public class PreCheckNewCardPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
