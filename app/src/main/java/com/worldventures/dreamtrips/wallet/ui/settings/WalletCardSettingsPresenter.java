package com.worldventures.dreamtrips.wallet.ui.settings;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard.WalletDisableDefaultCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.removecards.WalletAutoClearCardsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.WizardPinSetupPath;
import com.worldventures.dreamtrips.wallet.util.ThrowableHelper;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.Observable;

public class WalletCardSettingsPresenter extends WalletPresenter<WalletCardSettingsPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject ThrowableHelper throwableHelper;

   private SmartCard smartCard;

   public WalletCardSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeSmartCardChanges();

      observeStealthModeController(view);
      observeLockController(view);
      observeConnectionController(view);
   }

   private void observeSmartCardChanges() {
      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bindSmartCard(this.smartCard = command.getResult()));

      smartCardInteractor.stealthModePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(
                  OperationSubscriberWrapper.<SetStealthModeCommand>forView(getView().provideOperationDelegate())
                        .onSuccess(action -> stealthModeChangedMessage(action.stealthModeEnabled), action -> {})
                        .onFail(throwableHelper.provideMessageHolder(
                              command -> getView().stealthModeStatus(smartCard.stealthMode()))
                        )
                        .wrap()
            );

      smartCardInteractor.lockPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationSubscriberWrapper.<SetLockStateCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(setLockStateCommand -> {})
                  .onFail(getContext().getString(R.string.wallet_dashboard_unlock_error), a -> getView().lockStatus(smartCard
                        .lock()))
                  .wrap());
   }

   private void observeStealthModeController(Screen view) {
      view.stealthModeStatus()
            .compose(bindView())
            .skip(1)
            .filter(checkedFlag -> smartCard.stealthMode() != checkedFlag)
            .subscribe(this::stealthModeChanged);
   }

   private void observeLockController(Screen view) {
      view.lockStatus()
            .compose(bindView())
            .skip(1)
            .filter(lock -> smartCard.lock() != lock)
            .subscribe(this::lockStatusChanged);
   }

   private void observeConnectionController(Screen view) {
      view.testConnection()
            .compose(bindView())
            .skip(1)
            .filter(connected -> (smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED) != connected)
            .subscribe(this::manageConnection);
   }

   private void manageConnection(boolean connected) {
      if (connected) {
         smartCardInteractor.connectActionPipe()
               .createObservable(new ConnectSmartCardCommand(smartCard))
               .compose(bindViewIoToMainComposer())
               .subscribe(OperationSubscriberWrapper.<ConnectSmartCardCommand>forView(getView().provideOperationDelegate())
                     .onFail(throwableHelper.provideMessageHolder(
                           action -> getView().testConnection(smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED))
                     )
                     .wrap()
               );
      } else {
         smartCardInteractor.disconnectPipe()
               .createObservable(new DisconnectAction())
               .compose(bindViewIoToMainComposer())
               .subscribe(OperationSubscriberWrapper.<DisconnectAction>forView(getView().provideOperationDelegate())
                     .onFail(throwableHelper.provideMessageHolder(
                           action -> getView().testConnection(smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED))
                     )
                     .wrap()
               );
      }
   }

   private void bindSmartCard(SmartCard smartCard) {
      Screen view = getView();
      //noinspection all
      view.testConnection(smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED);
      view.stealthModeStatus(smartCard.stealthMode());
      view.lockStatus(smartCard.lock());
      view.disableDefaultPaymentValue(smartCard.disableCardDelay());
      view.autoClearSmartCardValue(smartCard.clearFlyeDelay());

   }

   public void goBack() {
      Flow.get(getContext()).goBack();
   }

   public void resetPin() {
      Flow.get(getContext()).set(new WizardPinSetupPath(smartCard, WizardPinSetupPath.Action.RESET));
   }

   public void disableDefaultCardTimer() {
      Flow.get(getContext()).set(new WalletDisableDefaultCardPath());
   }

   public void autoClearSmartCardClick() {
      Flow.get(getContext()).set(new WalletAutoClearCardsPath());
   }

   private void stealthModeChanged(boolean isEnabled) {
      smartCardInteractor.stealthModePipe().send(new SetStealthModeCommand(isEnabled));
   }

   private void lockStatusChanged(boolean lock) {
      smartCardInteractor.lockPipe().send(new SetLockStateCommand(lock));
   }

   private String stealthModeChangedMessage(boolean isEnabled) {
      return getContext().getString(
            isEnabled ? R.string.wallet_card_settings_stealth_mode_on : R.string.wallet_card_settings_stealth_mode_off
      );
   }

   public interface Screen extends WalletScreen {

      void stealthModeStatus(boolean isEnabled);

      void lockStatus(boolean lock);

      void testConnection(boolean connected);

      void disableDefaultPaymentValue(long millis);

      void autoClearSmartCardValue(long millis);

      Observable<Boolean> stealthModeStatus();

      Observable<Boolean> lockStatus();

      Observable<Boolean> testConnection();
   }
}
