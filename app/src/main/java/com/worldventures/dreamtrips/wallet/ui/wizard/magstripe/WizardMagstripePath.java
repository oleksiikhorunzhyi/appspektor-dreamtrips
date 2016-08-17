package com.worldventures.dreamtrips.wallet.ui.wizard.magstripe;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType;

@Layout(R.layout.wallet_wizard_magstripe_screen)
public class WizardMagstripePath extends StyledPath {

   public final CardType cardType;

   public WizardMagstripePath(CardType cardType) {
      this.cardType = cardType;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
