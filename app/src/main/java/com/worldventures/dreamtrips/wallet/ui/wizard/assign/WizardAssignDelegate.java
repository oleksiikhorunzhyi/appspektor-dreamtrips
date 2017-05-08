package com.worldventures.dreamtrips.wallet.ui.wizard.assign;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.NewCardSetupCompleteAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.SetupCompleteAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.SyncRecordsPath;

import java.util.Collections;
import java.util.List;

import flow.Flow;
import io.techery.janet.Command;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

abstract class WizardAssignDelegate {

   protected final WizardInteractor wizardInteractor;
   protected final RecordInteractor recordInteractor;
   protected final AnalyticsInteractor analyticsInteractor;
   protected final SmartCardInteractor smartCardInteractor;
   protected final Navigator navigator;

   private WizardAssignDelegate(WizardInteractor wizardInteractor, RecordInteractor recordInteractor, AnalyticsInteractor analyticsInteractor, SmartCardInteractor smartCardInteractor, Navigator navigator) {
      this.wizardInteractor = wizardInteractor;
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.smartCardInteractor = smartCardInteractor;
      this.navigator = navigator;
   }

   public static WizardAssignDelegate create(ProvisioningMode mode, WizardInteractor wizardInteractor, RecordInteractor recordInteractor,
         AnalyticsInteractor analyticsInteractor, SmartCardInteractor smartCardInteractor, Navigator navigator) {
      if (mode == ProvisioningMode.STANDARD) {
         return new WizardAssignDelegateStandard(wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, navigator);
      } else {
         return new WizardAssignDelegateNewCard(wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, navigator);
      }
   }

   protected abstract void toNextScreen(RxLifecycleView view);

   void onAssignUserSuccess(RxLifecycleView view) {
      sendAnalytic(new SetupCompleteAction());
      wizardInteractor.provisioningStatePipe().send(ProvisioningModeCommand.clear());
      toNextScreen(view);
   }

   protected final void sendAnalytic(WalletAnalyticsAction action) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(action));
   }

   private static final class WizardAssignDelegateStandard extends WizardAssignDelegate {

      private WizardAssignDelegateStandard(WizardInteractor wizardInteractor, RecordInteractor recordInteractor,
            AnalyticsInteractor analyticsInteractor, SmartCardInteractor smartCardInteractor, Navigator navigator) {
         super(wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, navigator);
      }

      @Override
      protected void toNextScreen(RxLifecycleView view) {
         navigator.single(new CardListPath(), Flow.Direction.REPLACE);
      }
   }

   private static final class WizardAssignDelegateNewCard extends WizardAssignDelegate {

      private WizardAssignDelegateNewCard(WizardInteractor wizardInteractor, RecordInteractor recordInteractor,
            AnalyticsInteractor analyticsInteractor, SmartCardInteractor smartCardInteractor, Navigator navigator) {
         super(wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, navigator);
      }

      @Override
      protected void toNextScreen(RxLifecycleView view) {
         fetchRecordList(view)
               .subscribe(records -> navigateToNextScreen(!records.isEmpty()));
      }

      private Observable<List<Record>> fetchRecordList(RxLifecycleView view) {
         return recordInteractor.cardsListPipe()
               .createObservableResult(new RecordListCommand())
               .map(Command::getResult)
               .onErrorReturn(throwable -> Collections.emptyList())
               .compose(view.lifecycle())
               .observeOn(AndroidSchedulers.mainThread());
      }

      private void navigateToNextScreen(boolean needToSyncPaymentCards) {
         if (needToSyncPaymentCards) {
            navigator.go(new SyncRecordsPath(SyncAction.TO_CARD));
         } else {
            finishSetupAndNavigateToDashboard();
         }
      }

      private void finishSetupAndNavigateToDashboard() {
         restoreOfflineModeDefaultState();
         activateSmartCard();

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
   }
}
