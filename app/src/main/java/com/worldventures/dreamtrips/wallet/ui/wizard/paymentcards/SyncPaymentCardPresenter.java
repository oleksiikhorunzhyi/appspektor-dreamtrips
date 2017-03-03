package com.worldventures.dreamtrips.wallet.ui.wizard.paymentcards;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.SyncPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SyncCardsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.paymentcomplete.PaymentSyncFinishPath;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class SyncPaymentCardPresenter extends WalletPresenter<SyncPaymentCardPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public SyncPaymentCardPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      analyticsInteractor
            .walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new SyncPaymentCardAction()));

      observeSyncPaymentCards();
   }

   private void observeSyncPaymentCards() {
      smartCardInteractor.cardSyncPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onStart(command -> getView().setProgressInPercent(0))
                  .onSuccess(command -> navigator.single(new PaymentSyncFinishPath(), Flow.Direction.REPLACE))
                  .create());

      smartCardInteractor.cardSyncPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .filter(action -> action.status == ActionState.Status.PROGRESS)
            .subscribe(new ActionStateSubscriber<SyncCardsCommand>()
                  .onProgress((command, progress) -> handleProgressSyncCard(command.getLocalOnlyCardsCount(), progress))
            );

      smartCardInteractor.cardSyncPipe().send(new SyncCardsCommand());
   }

   void onRetryCanceled() {
      smartCardInteractor.cardSyncPipe().send(new SyncCardsCommand());
   }

   private void handleProgressSyncCard(int countOfCards, int progress) {
      getView().setCountPaymentCardsProgress(progress, countOfCards);
      getView().setProgressInPercent(calcPercent(progress, countOfCards));
   }

   void finish() {
      navigator.finish();
   }

   private int calcPercent(int progress, int size) {
      return (int) ((100f / size) * progress);
   }

   public interface Screen extends WalletScreen {

      void setCountPaymentCardsProgress(int syncedCardsCount, int allCardsCount);

      void setProgressInPercent(int percent);

      OperationView<SyncCardsCommand> provideOperationView();
   }
}
