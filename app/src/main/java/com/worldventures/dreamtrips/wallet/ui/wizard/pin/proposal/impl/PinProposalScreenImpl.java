package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.impl;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog.PinProposalDialog;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class PinProposalScreenImpl extends WalletBaseController<PinProposalScreen, PinProposalPresenter> implements PinProposalScreen {

   private static final String KEY_PIN_PROPOSAL_ACTION = "key_pin_proposal_action";
   private static final String KEY_CARD_NICKNAME = "key_card_nickname";

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.not_now_button) TextView btnSkip;
   @InjectView(R.id.txt_pin_proposal_label) TextView tvLabel;

   @Inject PinProposalPresenter presenter;

   private PinProposalDialog pinProposalDialog;

   public static PinProposalScreenImpl create(PinProposalAction proposalAction) {
      return create(proposalAction, null);
   }

   public static PinProposalScreenImpl create(PinProposalAction proposalAction, String cardNickName) {
      final Bundle args = new Bundle();
      args.putSerializable(KEY_PIN_PROPOSAL_ACTION, proposalAction);
      if (cardNickName != null) {
         args.putString(KEY_CARD_NICKNAME, cardNickName);
      }
      return new PinProposalScreenImpl(args);
   }

   public PinProposalScreenImpl() {
      super();
   }

   public PinProposalScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void setLabel() {
      tvLabel.setText(getString(R.string.wallet_wizard_setup_pin_proposal_label));
   }

   @Override
   public void setLabelWithCardName(String cardNickName) {
      tvLabel.setText(getString(R.string.wallet_add_card_details_pin_proposal_label, cardNickName));
   }

   @Override
   public void setToolbarTitle(int toolbarTitleResId) {
      toolbar.setTitle(toolbarTitleResId);
   }

   @Override
   public void setSkipButtonLabel(int skipButtonResId) {
      btnSkip.setText(skipButtonResId);
   }

   @Override
   public void preparePinOptionalDialog(PinProposalDelegate pinProposalDelegate) {
      pinProposalDialog = pinProposalDelegate.createPinDialog(getPresenter(), null, getContext());
   }

   @Override
   public void showSkipPinDialog() {
      pinProposalDialog.showDialog();
   }

   @Override
   public void hideSkipPinDialog() {
      pinProposalDialog.hideDialog();
   }

   @Override
   public void disableToolbarNavigation() {
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @Override
   public void setupToolbarNavigation() {
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public PinProposalAction getPinProposalAction() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_PIN_PROPOSAL_ACTION))
            ? (PinProposalAction) getArgs().getSerializable(KEY_PIN_PROPOSAL_ACTION)
            : null;
   }

   @Override
   public String getCardNickName() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_CARD_NICKNAME))
            ? getArgs().getString(KEY_CARD_NICKNAME)
            : null;
   }

   @OnClick(R.id.create_pin_button)
   void onCreatePinClick() {
      getPresenter().handleCreatePin();
   }

   @OnClick(R.id.not_now_button)
   void onNotNowClick() {
      getPresenter().handleSkipPinCreation();
   }

   @Override
   public PinProposalPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_pin_proposal, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }
}
