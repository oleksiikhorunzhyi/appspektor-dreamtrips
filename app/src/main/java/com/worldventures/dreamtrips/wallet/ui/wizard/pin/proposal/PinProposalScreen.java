package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog.PinProposalDialog;

import butterknife.InjectView;
import butterknife.OnClick;


public class PinProposalScreen extends WalletLinearLayout<PinProposalPresenter.Screen, PinProposalPresenter, PinProposalPath>
      implements PinProposalPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.not_now_button) TextView btnSkip;
   @InjectView(R.id.txt_pin_proposal_label) TextView tvLabel;

   private PinProposalDialog pinProposalDialog;

   public PinProposalScreen(Context context) {
      this(context, null);
   }

   public PinProposalScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public PinProposalPresenter createPresenter() {
      return new PinProposalPresenter(getContext(), getInjector(), getPath());
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
      pinProposalDialog = pinProposalDelegate.createPinDialog(presenter, null, getContext());
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
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
   }

   @OnClick(R.id.create_pin_button)
   void onCreatePinClick() {
      presenter.handleCreatePin();
   }

   @OnClick(R.id.not_now_button)
   void onNotNowClick() {
      presenter.handleSkipPinCreation();
   }
}
