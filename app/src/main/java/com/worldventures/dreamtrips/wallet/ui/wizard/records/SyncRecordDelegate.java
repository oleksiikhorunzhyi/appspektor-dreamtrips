package com.worldventures.dreamtrips.wallet.ui.wizard.records;

import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordOnNewDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.paymentcomplete.PaymentSyncFinishPath;

import flow.Flow;
import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

abstract class SyncRecordDelegate {

   protected final SmartCardInteractor smartCardInteractor;
   protected final RecordInteractor recordInteractor;
   protected final Navigator navigator;

   private SyncRecordDelegate(SmartCardInteractor smartCardInteractor, RecordInteractor recordInteractor, Navigator navigator) {
      this.smartCardInteractor = smartCardInteractor;
      this.recordInteractor = recordInteractor;
      this.navigator = navigator;
   }

   public abstract void retry();

   public abstract void bindView(SyncView view);

   public static SyncRecordDelegate create(SyncAction action, SmartCardInteractor smartCardInteractor, RecordInteractor recordInteractor, Navigator navigator) {
      if (action == SyncAction.TO_DEVICE) {
         return new SyncToDeviceDelegate(smartCardInteractor, recordInteractor, navigator);
      } else {
         return new SyncToCardDelegate(smartCardInteractor, recordInteractor, navigator);
      }
   }

   private static class SyncToDeviceDelegate extends SyncRecordDelegate {

      private SyncToDeviceDelegate(SmartCardInteractor smartCardInteractor, RecordInteractor recordInteractor, Navigator navigator) {
         super(smartCardInteractor, recordInteractor, navigator);
      }

      @Override
      public void bindView(SyncView view) {
         observeSyncCard(view);
         view.hideProgressOfProcess();

         syncCards();
      }

      @Override
      public void retry() {
         syncCards();
      }

      private void syncCards() {
         recordInteractor.syncRecordOnNewDevicePipe().send(new SyncRecordOnNewDeviceCommand());
      }

      private void observeSyncCard(SyncView view) {
         recordInteractor.syncRecordOnNewDevicePipe()
               .observe()
               .compose(RxLifecycle.bindView(view.getView()))
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(OperationActionSubscriber.forView(view.<SyncRecordOnNewDeviceCommand>provideOperationView())
                     .onSuccess(command -> navigator.single(new PaymentSyncFinishPath(), Flow.Direction.REPLACE))
                     .create());
      }
   }

   private static class SyncToCardDelegate extends SyncRecordDelegate {

      private SyncToCardDelegate(SmartCardInteractor smartCardInteractor, RecordInteractor recordInteractor, Navigator navigator) {
         super(smartCardInteractor, recordInteractor, navigator);
      }

      @Override
      public void bindView(SyncView view) {
         observeOfflineModeStateCheck(view);
         observeSyncPaymentCards(view);
         observeSyncProgress(view);

         restoreOfflineModeDefaultState();
      }

      @Override
      public void retry() {
         restoreOfflineModeDefaultState();
      }

      private void restoreOfflineModeDefaultState() {
         smartCardInteractor.restoreOfflineModeDefaultStatePipe()
               .send(new RestoreOfflineModeDefaultStateCommand());
      }

      private void observeOfflineModeStateCheck(SyncView view) {
         smartCardInteractor.restoreOfflineModeDefaultStatePipe()
               .observe()
               .compose(RxLifecycle.bindView(view.getView()))
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(OperationActionSubscriber.forView(view.<RestoreOfflineModeDefaultStateCommand>provideOperationView())
                     .onStart(command -> view.setProgressInPercent(0))
                     .onSuccess(command -> recordInteractor.recordsSyncPipe().send(new SyncRecordsCommand()))
                     .create());
      }

      private void observeSyncPaymentCards(SyncView view) {
         recordInteractor.recordsSyncPipe()
               .observe()
               .compose(RxLifecycle.bindView(view.getView()))
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(OperationActionSubscriber.forView(view.<SyncRecordsCommand>provideOperationView())
                     .onSuccess(command -> navigator.single(new PaymentSyncFinishPath(), Flow.Direction.REPLACE))
                     .create());
      }

      private void observeSyncProgress(SyncView view) {
         recordInteractor.recordsSyncPipe()
               .observe()
               .filter(action -> action.status == ActionState.Status.PROGRESS)
               .compose(RxLifecycle.bindView(view.getView()))
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new ActionStateSubscriber<SyncRecordsCommand>()
                     .onProgress((command, progress) -> handleProgressSyncCard(view, command.getLocalOnlyRecordsCount(), progress))
               );
      }

      private void handleProgressSyncCard(SyncView view, int countOfCards, int progress) {
         view.setCountPaymentCardsProgress(progress, countOfCards);
         view.setProgressInPercent(calcPercent(progress, countOfCards));
      }

      private int calcPercent(int progress, int size) {
         return (int) ((100f / size) * progress);
      }
   }
}
