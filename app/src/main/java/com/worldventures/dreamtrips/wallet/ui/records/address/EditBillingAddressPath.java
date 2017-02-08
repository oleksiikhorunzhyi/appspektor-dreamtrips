package com.worldventures.dreamtrips.wallet.ui.records.address;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

// TODO: 2/3/17 rename to EditBillingAddressPath and move to records package
@Layout(R.layout.screen_wallet_wizard_edit_card_details)
public class EditBillingAddressPath extends StyledPath {
   private final BankCard bankCard;

   public EditBillingAddressPath(BankCard bankCard) {
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
