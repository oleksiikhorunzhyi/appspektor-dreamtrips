package com.worldventures.dreamtrips.wallet.ui.wizard.edit_card;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

@Layout(R.layout.screen_wallet_wizard_edit_card_details)
public class EditCardDetailsPath extends StyledPath {
   private final BankCard bankCard;

   public EditCardDetailsPath(BankCard bankCard) {
      this.bankCard = bankCard;
   }

   public BankCard getBankCard() {
      return bankCard;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

}
