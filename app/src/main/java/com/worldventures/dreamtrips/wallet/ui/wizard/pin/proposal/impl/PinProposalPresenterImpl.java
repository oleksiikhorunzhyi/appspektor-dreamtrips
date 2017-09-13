package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.impl;


import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.provisioning.PinOptionalCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalScreen;

public class PinProposalPresenterImpl extends WalletPresenterImpl<PinProposalScreen> implements PinProposalPresenter {

   private final WizardInteractor wizardInteractor;
   private PinProposalDelegate pinProposalDelegate;

   public PinProposalPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WizardInteractor wizardInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.wizardInteractor = wizardInteractor;
   }

   @Override
   public void attachView(PinProposalScreen view) {
      super.attachView(view);
      final PinProposalAction pinProposalAction = getView().getPinProposalAction();
      final String cardNickName = getView().getCardNickName();
      this.pinProposalDelegate = PinProposalDelegate.create(getNavigator(), pinProposalAction, cardNickName, wizardInteractor);
      pinProposalDelegate.setupView(view);
      getView().preparePinOptionalDialog(pinProposalDelegate);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
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
