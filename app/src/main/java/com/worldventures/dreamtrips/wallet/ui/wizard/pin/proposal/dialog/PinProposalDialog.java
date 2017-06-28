package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog;


import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalPresenter;

public abstract class PinProposalDialog {

   protected final PinProposalPresenter presenter;

   public PinProposalDialog(PinProposalPresenter presenter) {
      this.presenter = presenter;
   }

   public final void remindLater() {
      presenter.remindMeLater();
   }

   public final void cancel() {
      presenter.cancelDialog();
   }

   public abstract void showDialog();

   public abstract void hideDialog();
}
