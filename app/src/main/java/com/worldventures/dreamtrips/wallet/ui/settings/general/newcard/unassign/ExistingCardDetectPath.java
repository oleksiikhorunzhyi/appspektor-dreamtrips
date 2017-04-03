package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.unassign;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_existing_card_detect)
public class ExistingCardDetectPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
