package com.worldventures.dreamtrips.wallet.ui.records.tokenize_migration;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.TokenizeRecordsMigrationCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class TokenizeRecordsMigrationPresenter extends WalletPresenter<TokenizeRecordsMigrationPresenter.Screen, Parcelable> {

   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SmartCardInteractor interactor;

   @Inject Navigator navigator;

   public TokenizeRecordsMigrationPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();

      interactor.tokenizeMigrationPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onProgress(command -> getView().showMigrateCardsCount(command.getMigrateCardsCount()))
                  .onSuccess(command -> handleSuccess())
                  .create()
            );

      migrate();
   }

   void retry() {
      migrate();
   }

   void cancelMigration() {
      navigator.finish();
   }

   private void migrate() {
      interactor.tokenizeMigrationPipe().send(new TokenizeRecordsMigrationCommand());
   }

   private void handleSuccess() {
      navigator.single(new CardListPath(), Flow.Direction.REPLACE);
   }

   public interface Screen extends WalletScreen {

      OperationView<TokenizeRecordsMigrationCommand> provideOperationView();

      void showMigrateCardsCount(int count);
   }

}