package com.worldventures.dreamtrips.wallet.ui.wizard.finish;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.NewCardSetupCompleteAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.SetupCompleteAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncRecordsPath;

import java.util.Collections;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WizardAssignUserPresenter extends WalletPresenter<WizardAssignUserPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject RecordInteractor recordInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public WizardAssignUserPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeComplete();
      onWizardComplete();
   }

   void onWizardComplete() {
      wizardInteractor.completePipe().send(new WizardCompleteCommand());
   }

   void onWizardCancel() {
      navigator.goBack();
   }

   private void observeComplete() {
      wizardInteractor.completePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> {
                     sendAnalytic(new SetupCompleteAction());
                     prepareToNextScreen();
                  })
                  .create());
   }

   private void prepareToNextScreen() {
      recordInteractor.cardsListPipe()
            .createObservableResult(new RecordListCommand())
            .map(Command::getResult)
            .onErrorReturn(throwable -> Collections.emptyList())
            .compose(bindViewIoToMainComposer())
            .subscribe(records -> navigateToNextScreen(!records.isEmpty()));
   }

   private void navigateToNextScreen(boolean needToSyncPaymentCards) {
      if (needToSyncPaymentCards) {
         navigator.go(new SyncRecordsPath());
      } else {
         finishSetupAndNavigateToDashboard();
      }
   }

   private void finishSetupAndNavigateToDashboard() {
      restoreOfflineModeDefaultState();
      activateSmartCard();
      sendAnalytic(new NewCardSetupCompleteAction());

      navigator.single(new CardListPath(), Flow.Direction.REPLACE);
   }

   private void restoreOfflineModeDefaultState() {
      smartCardInteractor.restoreOfflineModeDefaultStatePipe()
            .send(new RestoreOfflineModeDefaultStateCommand());
   }

   private void activateSmartCard() {
      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand(sc ->
            ImmutableSmartCard.builder().from(sc).cardStatus(SmartCard.CardStatus.ACTIVE).build()));
   }

   private void sendAnalytic(WalletAnalyticsAction action) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(action));
   }

   interface Screen extends WalletScreen {

      OperationView<WizardCompleteCommand> provideOperationView();
   }
}
