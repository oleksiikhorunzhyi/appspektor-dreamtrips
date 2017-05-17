package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog;


import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalPresenter;

public class WizardPinProposalDialog extends PinProposalDialog {

   private final MaterialDialog dialog;

   public WizardPinProposalDialog(PinProposalPresenter presenter, Context context) {
      super(presenter);
      this.dialog = new MaterialDialog.Builder(context)
            .content(R.string.wallet_wizard_setup_pin_proposal_dialog_content)
            .positiveText(R.string.wallet_continue_label)
            .negativeText(R.string.wallet_cancel_label)
            .onPositive((dialog, which) -> remindLater())
            .onNegative((dialog, which) -> hideDialog())
            .build();
   }

   @Override
   public void showDialog() {
      dialog.show();
   }

   @Override
   public void hideDialog() {
      dialog.hide();
   }
}
