package com.worldventures.dreamtrips.wallet.ui.settings.newcard.unassign;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.helper.CardIdUtil;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.pin.EnterPinUnassignPath;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.poweron.NewCardPowerOnPath;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.success.UnassignSuccessPath;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import timber.log.Timber;

public class ExistingCardDetectPresenter extends WalletPresenter<ExistingCardDetectPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;

   public ExistingCardDetectPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observerSmartCardConnectedStatus();
      fetchSmartCardId();
   }

   private void fetchSmartCardId() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> bindSmartCardId(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
            );
   }

   private void bindSmartCardId(String smartCardId) {
      getView().setSmartCardId(CardIdUtil.pushZeroToSmartCardId(smartCardId));
   }

   private void observerSmartCardConnectedStatus() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> handleConnectedResult(command.getResult().connectionStatus()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
                  .create());
   }

   private void handleConnectedResult(SmartCard.ConnectionStatus connectionStatus) {
      if (connectionStatus.isConnected()) {
         getView().modeConnectedSmartCard();
      } else {
         getView().modeDisconnectedSmartCard();
      }
   }

   void unassignCard() {
      navigator.go(new EnterPinUnassignPath());
   }

   void prepareUnassignCard() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> getView().showConfirmationUnassignDialog(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
                  .create());
   }

   void prepareUnassignCardOnBackend() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> getView().showConfirmationUnassignOnBackend(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
                  .create());
   }

   void unassignCardOnBackend() {
      smartCardInteractor.wipeSmartCardDataCommandActionPipe()
            .createObservable(new WipeSmartCardDataCommand(false))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideWipeOperationView())
                  .onSuccess(activeSmartCardCommand -> navigator.single(new UnassignSuccessPath()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, "unassignCardOnBackend()"))
                  .create());
   }

   public void goBack() {
      navigator.goBack();
   }

   void navigateToPowerOn() {
      navigator.go(new NewCardPowerOnPath());
   }

   public interface Screen extends WalletScreen {

      OperationView<ActiveSmartCardCommand> provideOperationView();

      OperationView<WipeSmartCardDataCommand> provideWipeOperationView();

      void setSmartCardId(String scId);

      void modeConnectedSmartCard();

      void modeDisconnectedSmartCard();

      void showConfirmationUnassignDialog(String scId);

      void showConfirmationUnassignOnBackend(String scId);
   }
}
