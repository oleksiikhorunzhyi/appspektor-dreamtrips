package com.worldventures.wallet.ui.wizard.assign;

import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.wizard.SetupCompleteAction;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.wallet.service.command.RecordListCommand;
import com.worldventures.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.pin.proposal.PinProposalAction;
import com.worldventures.wallet.ui.wizard.records.SyncAction;

import java.util.Collections;
import java.util.List;

import io.techery.janet.Command;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public abstract class WizardAssignDelegate {

   protected final WizardInteractor wizardInteractor;
   protected final RecordInteractor recordInteractor;
   protected final WalletAnalyticsInteractor analyticsInteractor;
   protected final SmartCardInteractor smartCardInteractor;
   protected final Navigator navigator;

   private WizardAssignDelegate(WizardInteractor wizardInteractor, RecordInteractor recordInteractor, WalletAnalyticsInteractor analyticsInteractor,
         SmartCardInteractor smartCardInteractor, Navigator navigator) {
      this.wizardInteractor = wizardInteractor;
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.smartCardInteractor = smartCardInteractor;
      this.navigator = navigator;
   }

   public static WizardAssignDelegate create(ProvisioningMode mode, WizardInteractor wizardInteractor, RecordInteractor recordInteractor,
         WalletAnalyticsInteractor analyticsInteractor, SmartCardInteractor smartCardInteractor, Navigator navigator) {
      if (mode == ProvisioningMode.STANDARD) {
         return new WizardAssignDelegateStandard(wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, navigator);
      } else {
         return new WizardAssignDelegateNewCard(wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, navigator);
      }
   }

   protected abstract void toNextScreen(WizardAssignUserScreen view);

   public void onAssignUserSuccess(WizardAssignUserScreen view) {
      sendAnalytic(new SetupCompleteAction());
      wizardInteractor.provisioningStatePipe().send(ProvisioningModeCommand.clear());
      toNextScreen(view);
   }

   protected final void sendAnalytic(WalletAnalyticsAction action) {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(action));
   }

   protected void activateSmartCard() {
      wizardInteractor.activateSmartCardPipe().send(new ActivateSmartCardCommand());
   }

   private static final class WizardAssignDelegateStandard extends WizardAssignDelegate {

      private WizardAssignDelegateStandard(WizardInteractor wizardInteractor, RecordInteractor recordInteractor,
            WalletAnalyticsInteractor analyticsInteractor, SmartCardInteractor smartCardInteractor, Navigator navigator) {
         super(wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, navigator);
      }

      @Override
      protected void toNextScreen(WizardAssignUserScreen view) {
         navigator.goPinProposalUserSetup(PinProposalAction.WIZARD);
      }
   }

   private static final class WizardAssignDelegateNewCard extends WizardAssignDelegate {

      private WizardAssignDelegateNewCard(WizardInteractor wizardInteractor, RecordInteractor recordInteractor,
            WalletAnalyticsInteractor analyticsInteractor, SmartCardInteractor smartCardInteractor, Navigator navigator) {
         super(wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, navigator);
      }

      @Override
      protected void toNextScreen(WizardAssignUserScreen view) {
         fetchRecordList(view)
               .subscribe(records -> navigateToNextScreen(!records.isEmpty()));
      }

      private Observable<List<Record>> fetchRecordList(WizardAssignUserScreen view) {
         return recordInteractor.cardsListPipe()
               .createObservableResult(new RecordListCommand())
               .map(Command::getResult)
               .onErrorReturn(throwable -> Collections.emptyList())
               .compose(RxLifecycleAndroid.bindView(view.getView()))
               .observeOn(AndroidSchedulers.mainThread());
      }

      private void navigateToNextScreen(boolean needToSyncPaymentCards) {
         if (needToSyncPaymentCards) {
            navigator.goSyncRecordsPath(SyncAction.TO_CARD);
         } else {
            finishSetupAndNavigateToDashboard();
         }
      }

      private void finishSetupAndNavigateToDashboard() {
         restoreOfflineModeDefaultState();
         activateSmartCard();
         navigator.goCardList();
      }

      private void restoreOfflineModeDefaultState() {
         smartCardInteractor.restoreOfflineModeDefaultStatePipe()
               .send(new RestoreOfflineModeDefaultStateCommand());
      }
   }
}
