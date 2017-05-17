package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal;


import android.content.Context;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.assign.WizardAssignUserPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.EnterPinPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog.PinProposalDialog;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog.RecordsPinProposalDialog;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.dialog.WizardPinProposalDialog;

import flow.path.Path;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public abstract class PinProposalDelegate<T extends PinProposalDialog> {
   private final Navigator navigator;

   private PinProposalDelegate(Navigator navigator) {
      this.navigator = navigator;
   }

   public static PinProposalDelegate create(Navigator navigator, PinProposalPath proposalPath, WizardInteractor wizardInteractor) {
      if (proposalPath.getProposalAction() == PinProposalAction.WIZARD) {
         return new WizardPinProposalDelegate(navigator, wizardInteractor);
      } else {
         return new RecordsPinProposalDelegate(navigator, proposalPath.getCardNickName());
      }
   }

   protected void setupView(PinProposalPresenter.Screen pinProposalScreen) {
      pinProposalScreen.setToolbarTitle(getToolbarResId());
      pinProposalScreen.setSkipButtonLabel(getSkipButtonResId());
      setTextLabel(pinProposalScreen);
   }

   protected void navigateCreatePin() {
      determineCreatePinBehavior(navigator, provideCreatePinPath());
   }

   protected void navigateSkipPin() {
      determineSkipBehavior(navigator);
   }

   public abstract T createPinDialog(PinProposalPresenter presenter, View bottomSheetView, Context context);

   protected abstract void setTextLabel(PinProposalPresenter.Screen pinProposalScreen);

   public abstract int getToolbarResId();

   public abstract int getSkipButtonResId();

   public abstract Path provideCreatePinPath();

   public abstract void determineSkipBehavior(Navigator navigator);

   public abstract void determineCreatePinBehavior(Navigator navigator, Path path);

   private static class WizardPinProposalDelegate extends PinProposalDelegate<WizardPinProposalDialog> {
      private final WizardInteractor wizardInteractor;
      private Path skipPinPath;

      private WizardPinProposalDelegate(Navigator navigator, WizardInteractor wizardInteractor) {
         super(navigator);
         this.wizardInteractor = wizardInteractor;
      }

      @Override
      protected void setupView(PinProposalPresenter.Screen pinProposalScreen) {
         super.setupView(pinProposalScreen);
         pinProposalScreen.setupToolbarNavigation();
         checkProvisionMode(pinProposalScreen);
      }

      private void checkProvisionMode(PinProposalPresenter.Screen pinProposalScreen) {
         wizardInteractor.provisioningStatePipe()
               .createObservable(ProvisioningModeCommand.fetchState())
               .compose(pinProposalScreen.lifecycle())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new ActionStateSubscriber<ProvisioningModeCommand>()
                     .onSuccess(command -> this.skipPinPath = new WizardAssignUserPath(command.getResult())));
      }

      @Override
      public WizardPinProposalDialog createPinDialog(PinProposalPresenter presenter, View bottomSheetView, Context context) {
         return new WizardPinProposalDialog(presenter, context);
      }

      @Override
      protected void setTextLabel(PinProposalPresenter.Screen pinProposalScreen) {
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
      public Path provideCreatePinPath() {
         return new EnterPinPath(Action.SETUP);
      }

      @Override
      public void determineSkipBehavior(Navigator navigator) {
         navigator.withoutLast(skipPinPath);
      }

      @Override
      public void determineCreatePinBehavior(Navigator navigator, Path path) {
         navigator.go(path);
      }
   }

   private static class RecordsPinProposalDelegate extends PinProposalDelegate<RecordsPinProposalDialog> {
      private final String cardNickname;

      private RecordsPinProposalDelegate(Navigator navigator, String cardNickname) {
         super(navigator);
         this.cardNickname = cardNickname;
      }

      @Override
      protected void setupView(PinProposalPresenter.Screen pinProposalScreen) {
         super.setupView(pinProposalScreen);
         pinProposalScreen.disableToolbarNavigation();
      }

      @Override
      public RecordsPinProposalDialog createPinDialog(PinProposalPresenter presenter, View bottomSheetView, Context context) {
         return new RecordsPinProposalDialog(presenter, bottomSheetView);
      }

      @Override
      protected void setTextLabel(PinProposalPresenter.Screen pinProposalScreen) {
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
      public Path provideCreatePinPath() {
         return new EnterPinPath(Action.ADD);
      }

      @Override
      public void determineSkipBehavior(Navigator navigator) {
         navigator.goBack();
      }

      @Override
      public void determineCreatePinBehavior(Navigator navigator, Path path) {
         navigator.withoutLast(path);
      }
   }
}
