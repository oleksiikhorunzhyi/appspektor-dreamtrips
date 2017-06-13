package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.provisioning.PinOptionalCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;


public class PinProposalPresenter extends WalletPresenter<PinProposalPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   private final PinProposalDelegate pinProposalDelegate;

   public PinProposalPresenter(Context context, Injector injector, PinProposalPath pinProposalPath) {
      super(context, injector);
      this.pinProposalDelegate = PinProposalDelegate.create(navigator, pinProposalPath, wizardInteractor);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      pinProposalDelegate.setupView(view);
      getView().preparePinOptionalDialog(pinProposalDelegate);
   }

   void goBack() {
      navigator.goBack();
   }

   void handleCreatePin() {
      pinProposalDelegate.navigateCreatePin();
   }

   void handleSkipPinCreation() {
      getView().showSkipPinDialog();
   }

   public void cancelDialog() {
      getView().hideSkipPinDialog();
   }

   public void dontShowAgain() {
      wizardInteractor.pinOptionalActionPipe().send(PinOptionalCommand.save(false));
      pinProposalDelegate.navigateSkipPin();
   }

   public void remindMeLater() {
      pinProposalDelegate.navigateSkipPin();
   }

   public interface Screen extends WalletScreen {

      void setLabel();

      void setLabelWithCardName(String cardNickName);

      void setToolbarTitle(int toolbarTitleResId);

      void setSkipButtonLabel(int skipButtonResId);

      void preparePinOptionalDialog(PinProposalDelegate pinProposalDelegate);

      void showSkipPinDialog();

      void hideSkipPinDialog();

      void disableToolbarNavigation();

      void setupToolbarNavigation();
   }
}
