package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_pin_proposal)
public class PinProposalPath extends StyledPath {

   private final PinProposalAction proposalAction;
   private final String cardNickName;

   public PinProposalPath(PinProposalAction pinProposalAction) {
      this.proposalAction = pinProposalAction;
      this.cardNickName = null;
   }

   public PinProposalPath(PinProposalAction proposalAction, String cardNickName) {
      this.proposalAction = proposalAction;
      this.cardNickName = cardNickName;
   }

   public PinProposalAction getProposalAction() {
      return proposalAction;
   }

   public String getCardNickName() {
      return cardNickName;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
