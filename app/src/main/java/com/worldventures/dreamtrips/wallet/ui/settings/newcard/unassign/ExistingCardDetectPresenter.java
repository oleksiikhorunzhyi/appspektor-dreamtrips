package com.worldventures.dreamtrips.wallet.ui.settings.newcard.unassign;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.pin.EnterPinUnassignPath;

import javax.inject.Inject;

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
   }

   private void observerSmartCardConnectedStatus() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> {
                     if (command.getResult().connectionStatus().isConnected()) {
                        getView().modeConnectedSmartCard();
                     } else {
                        getView().modeDisconnectedSmartCard();
                     }
                  })
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
                  .create());
   }

   public void unassignCard() {
      navigator.go(new EnterPinUnassignPath());
   }

   public void prepareUnassignCard() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> getView().showConfirmationUnassignDialog(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
                  .create());
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      OperationView<ActiveSmartCardCommand> provideOperationView();

      void setSmartCardId(String scId);

      void modeConnectedSmartCard();

      void modeDisconnectedSmartCard();

      void showConfirmationUnassignDialog(String scId);
   }
}
