package com.worldventures.wallet.ui.wizard.pin.proposal.dialog;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;

import com.worldventures.wallet.BR;
import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.wizard.pin.proposal.PinProposalPresenter;

public class RecordsPinProposalDialog extends PinProposalDialog {

   private final BottomSheetDialog bottomSheetDialog;

   public RecordsPinProposalDialog(Context context, PinProposalPresenter presenter) {
      super(presenter);
      bottomSheetDialog = new BottomSheetDialog(context);

      ViewDataBinding viewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_wallet_pin_proposal_bottom_sheet,
            null,
            false
      );

      viewDataBinding.setVariable(BR.recordBottomDialog, this);

      bottomSheetDialog.setContentView(viewDataBinding.getRoot());
   }

   public void onRemindLaterClick() {
      hideDialog();
      remindLater();
   }

   public void onDontShowClick() {
      hideDialog();
      presenter.dontShowAgain();
   }

   public void onCancelClick() {
      hideDialog();
   }

   @Override
   public void showDialog() {
      bottomSheetDialog.show();
   }

   @Override
   public void hideDialog() {
      bottomSheetDialog.cancel();
   }
}
