package com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.impl;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryDialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.SyncRecordsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.SyncRecordsScreen;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static android.view.View.INVISIBLE;

public class SyncRecordsScreenImpl extends WalletBaseController<SyncRecordsScreen, SyncRecordsPresenter> implements SyncRecordsScreen{

   private static final String KEY_SYNC_ACTION = "key_sync_action";

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_progress_count_cards_sync) TextView progressCountCardsSync;
   @InjectView(R.id.tv_progress_status) TextView progressPercentageLabel;
   @InjectView(R.id.firmware_install_progress) WalletProgressWidget installProgress;

   @Inject SyncRecordsPresenter presenter;

   private MaterialDialog retrySyncCardsDialog = null;

   public static SyncRecordsScreenImpl create(SyncAction syncAction) {
      final Bundle args = new Bundle();
      args.putSerializable(KEY_SYNC_ACTION, syncAction);
      return new SyncRecordsScreenImpl(args);
   }

   public SyncRecordsScreenImpl() {
      super();
   }

   public SyncRecordsScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_sync_records, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void setCountPaymentCardsProgress(int syncedCardsCount, int allCardsCount) {
      progressCountCardsSync.setText(getString(R.string.wallet_syncing_payment_cards_count, syncedCardsCount, allCardsCount));
   }

   @Override
   public void setProgressInPercent(int percent) {
      progressPercentageLabel.setText(String.format(Locale.US, "%d%%", percent));
   }

   @Override
   public <T> OperationView<T> provideOperationView() {
      return new ComposableOperationView<>(
            new WalletProgressView<>(installProgress),
            new RetryDialogErrorView<>(getContext(), R.string.wallet_syncing_payment_cards_fail_msg,
                  command -> getPresenter().retrySync(),
                  command -> getPresenter().navigateToWallet())
      );
   }

   @Override
   public void hideProgressOfProcess() {
      progressPercentageLabel.setVisibility(INVISIBLE);
      progressCountCardsSync.setText(R.string.wallet_syncing_payment_cards_message);
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (retrySyncCardsDialog != null) retrySyncCardsDialog.dismiss();
      super.onDetach(view);
   }

   @Override
   public SyncRecordsPresenter getPresenter() {
      return presenter;
   }

   @Override
   public SyncAction getSyncAction() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_SYNC_ACTION))
            ? (SyncAction) getArgs().getSerializable(KEY_SYNC_ACTION)
            : null;
   }
}
