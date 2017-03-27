package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.CardConnectedAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.CheckFrontAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PhotoWasSetAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup.WizardPinSetupPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePath;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class PairKeyPresenter extends WalletPresenter<PairKeyPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SmartCardInteractor smartCardInteractor;

   private final String barcode;

   public PairKeyPresenter(Context context, Injector injector, String barcode) {
      super(context, injector);
      this.barcode = barcode;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new CheckFrontAction()));

      observeCreateAndConnectSmartCard();
      observeSetupUserSmartCardData();
   }

   private void observeSetupUserSmartCardData() {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupUserDataPipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetupUserData())
                  .onSuccess(setupUserDataCommand -> onUserSetupSuccess())
                  .create());
   }
   /*.handle(FirstNameException.class, R.string.wallet_edit_profile_first_name_format_detail)
                        .handle(LastNameException.class, R.string.wallet_edit_profile_last_name_format_detail)
                        .handle(MiddleNameException.class, R.string.wallet_edit_profile_middle_name_format_detail)
                        .handle(SetupUserDataCommand.MissedAvatarException.class, R.string.wallet_edit_profile_avatar_not_chosen)*/

   private void observeCreateAndConnectSmartCard() {
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(OperationActionStateSubscriberWrapper.<CreateAndConnectToCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> smartCardConnected())
                  .onFail(ErrorHandler.<CreateAndConnectToCardCommand>builder(getContext())
                        .handle(SmartCardConnectException.class, R.string.wallet_smartcard_connection_error)
                        .defaultAction(command -> goBack())
                        .build())
                  .wrap());
   }

   private void smartCardConnected() {
      smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(this::handleSmartCardUserExisting);
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new CardConnectedAction()));
   }

   private void handleSmartCardUserExisting(SmartCardUser smartCardUser) {
      if (smartCardUser != null) {
         wizardInteractor.setupUserDataPipe().send(new SetupUserDataCommand(
                     smartCardUser.firstName(),
                     smartCardUser.middleName(),
                     smartCardUser.lastName(),
                     smartCardUser.userPhoto())
         );
      } else {
         navigator.withoutLast(new WizardEditProfilePath());
      }
   }

   private void onUserSetupSuccess() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new PhotoWasSetAction()));
      navigator.go(new WizardPinSetupPath(Action.SETUP));
   }

   void tryToPairAndConnectSmartCard() {
      wizardInteractor.createAndConnectActionPipe().send(new CreateAndConnectToCardCommand(barcode));
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      OperationView<SetupUserDataCommand> provideOperationSetupUserData();
   }
}
