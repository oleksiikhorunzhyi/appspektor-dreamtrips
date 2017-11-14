package com.worldventures.wallet.ui.wizard.pin.proposal.impl;

import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.provisioning.PinOptionalCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.pin.proposal.PinProposalAction;
import com.worldventures.wallet.ui.wizard.pin.proposal.PinProposalDelegate;
import com.worldventures.wallet.ui.wizard.pin.proposal.PinProposalPresenter;
import com.worldventures.wallet.ui.wizard.pin.proposal.PinProposalScreen;

public class PinProposalPresenterImpl extends WalletPresenterImpl<PinProposalScreen> implements PinProposalPresenter {

   private final WizardInteractor wizardInteractor;
   private PinProposalDelegate pinProposalDelegate;
   private PinProposalAction pinProposalAction;

   public PinProposalPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WizardInteractor wizardInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.wizardInteractor = wizardInteractor;
   }

   @Override
   public void attachView(PinProposalScreen view) {
      super.attachView(view);
      pinProposalAction = getView().getPinProposalAction();
      final String cardNickName = getView().getCardNickName();
      this.pinProposalDelegate = PinProposalDelegate.create(getNavigator(), pinProposalAction, cardNickName);
      pinProposalDelegate.setupView(view);
      getView().preparePinOptionalDialog(pinProposalDelegate);
   }

   @Override
   public void goBack() {
      if (pinProposalAction == PinProposalAction.RECORDS) {
         getNavigator().goBackWithoutHandler();
      } else {
         getNavigator().goSuccessProvisioningWithRevertAnimation();
      }
   }

   @Override
   public void handleCreatePin() {
      pinProposalDelegate.navigateCreatePin();
   }

   @Override
   public void handleSkipPinCreation() {
      getView().showSkipPinDialog();
   }

   @Override
   public void cancelDialog() {
      getView().hideSkipPinDialog();
   }

   @Override
   public void dontShowAgain() {
      wizardInteractor.pinOptionalActionPipe().send(PinOptionalCommand.save(false));
      pinProposalDelegate.navigateSkipPin();
   }

   @Override
   public void remindMeLater() {
      pinProposalDelegate.navigateSkipPin();
   }
}
