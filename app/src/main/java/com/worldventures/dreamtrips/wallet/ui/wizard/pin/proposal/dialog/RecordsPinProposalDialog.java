package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog;


import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalPresenter;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordsPinProposalDialog extends PinProposalDialog {

   private final BottomSheetBehavior bottomSheetBehavior;

   public RecordsPinProposalDialog(PinProposalPresenter presenter, View bottomSheetView) {
      super(presenter);
      bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      ButterKnife.inject(this, bottomSheetView);
   }

   @OnClick(R.id.txt_pin_proposal_remind_later)
   void onRemindLaterClick() {
      remindLater();
   }

   @OnClick(R.id.txt_pin_proposal_dont_show)
   void onDontShowClick() {
      presenter.dontShowAgain();
   }

   @OnClick(R.id.txt_pin_proposal_cancel)
   void onCancelClick() {
      hideDialog();
   }

   @Override
   public void showDialog() {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
   }

   @Override
   public void hideDialog() {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
   }
}
