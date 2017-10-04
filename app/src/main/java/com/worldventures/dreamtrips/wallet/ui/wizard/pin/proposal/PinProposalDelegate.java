package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal;


import android.content.Context;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog.PinProposalDialog;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog.RecordsPinProposalDialog;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog.WizardPinProposalDialog;

public abstract class PinProposalDelegate<T extends PinProposalDialog> {
   private final Navigator navigator;

   private PinProposalDelegate(Navigator navigator) {
      this.navigator = navigator;
   }

   public static PinProposalDelegate create(Navigator navigator, PinProposalAction pinProposalAction, String cardNickName) {
      if (pinProposalAction == PinProposalAction.WIZARD) {
         return new WizardPinProposalDelegate(navigator);
      } else {
         return new RecordsPinProposalDelegate(navigator, cardNickName);
      }
   }

   public void setupView(PinProposalScreen pinProposalScreen) {
      pinProposalScreen.setToolbarTitle(getToolbarResId());
      pinProposalScreen.setSkipButtonLabel(getSkipButtonResId());
      setTextLabel(pinProposalScreen);
   }

   public Navigator getNavigator() {
      return navigator;
   }

   public abstract T createPinDialog(PinProposalPresenter presenter, View bottomSheetView, Context context);

   protected abstract void setTextLabel(PinProposalScreen pinProposalScreen);

   public abstract int getToolbarResId();

   public abstract int getSkipButtonResId();

   public abstract void navigateCreatePin();

   public abstract void navigateSkipPin();

   private static class WizardPinProposalDelegate extends PinProposalDelegate<WizardPinProposalDialog> {

      private WizardPinProposalDelegate(Navigator navigator) {
         super(navigator);
      }

      @Override
      public void setupView(PinProposalScreen pinProposalScreen) {
         super.setupView(pinProposalScreen);
         pinProposalScreen.disableToolbarNavigation();
      }

      @Override
      public WizardPinProposalDialog createPinDialog(PinProposalPresenter presenter, View bottomSheetView, Context context) {
         return new WizardPinProposalDialog(presenter, context);
      }

      @Override
      protected void setTextLabel(PinProposalScreen pinProposalScreen) {
         pinProposalScreen.setLabel();
      }

      @Override
      public int getToolbarResId() {
         return R.string.wallet_wizard_setup_pin_proposal_toolbar;
      }

      @Override
      public int getSkipButtonResId() {
         return R.string.wallet_wizard_setup_pin_proposal_skip;
      }

      @Override
      public void navigateSkipPin() {
         getNavigator().goPaymentSyncFinished();
      }

      @Override
      public void navigateCreatePin() {
         getNavigator().goEnterPinProposalWizard();
      }
   }

   private static class RecordsPinProposalDelegate extends PinProposalDelegate<RecordsPinProposalDialog> {
      private final String cardNickname;

      private RecordsPinProposalDelegate(Navigator navigator, String cardNickname) {
         super(navigator);
         this.cardNickname = cardNickname;
      }

      @Override
      public void setupView(PinProposalScreen pinProposalScreen) {
         super.setupView(pinProposalScreen);
         pinProposalScreen.disableToolbarNavigation();
      }

      @Override
      public RecordsPinProposalDialog createPinDialog(PinProposalPresenter presenter, View bottomSheetView, Context context) {
         return new RecordsPinProposalDialog(context, presenter);
      }

      @Override
      protected void setTextLabel(PinProposalScreen pinProposalScreen) {
         pinProposalScreen.setLabelWithCardName(cardNickname);
      }

      @Override
      public int getToolbarResId() {
         return R.string.wallet_add_card_details_pin_proposal_toolbar;
      }

      @Override
      public int getSkipButtonResId() {
         return R.string.wallet_add_card_details_pin_proposal_no_thanks;
      }

      @Override
      public void navigateSkipPin() {
         getNavigator().goBack();
      }

      @Override
      public void navigateCreatePin() {
         getNavigator().goEnterPinProposalRecords();
      }
   }
}
