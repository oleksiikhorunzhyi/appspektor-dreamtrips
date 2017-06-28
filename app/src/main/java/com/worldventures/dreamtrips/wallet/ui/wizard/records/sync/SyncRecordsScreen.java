package com.worldventures.dreamtrips.wallet.ui.wizard.records.sync;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryErrorDialogView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import java.util.Locale;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class SyncRecordsScreen extends WalletLinearLayout<SyncRecordsPresenter.Screen, SyncRecordsPresenter, SyncRecordsPath> implements SyncRecordsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_progress_count_cards_sync) TextView progressCountCardsSync;
   @InjectView(R.id.tv_progress_status) TextView progressPercentageLabel;
   @InjectView(R.id.firmware_install_progress) WalletProgressWidget installProgress;

   private MaterialDialog retrySyncCardsDialog = null;

   public SyncRecordsScreen(Context context) {
      super(context);
   }

   public SyncRecordsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @NonNull
   @Override
   public SyncRecordsPresenter createPresenter() {
      return new SyncRecordsPresenter(getContext(), getInjector(), getPath().syncAction);
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
            new RetryErrorDialogView<>(getContext(), R.string.wallet_syncing_payment_cards_fail_msg,
                  command -> presenter.retrySync(),
                  command -> presenter.navigateToWallet())
      );
   }

   @Override
   public void hideProgressOfProcess() {
      progressPercentageLabel.setVisibility(INVISIBLE);
      progressCountCardsSync.setText(R.string.wallet_syncing_payment_cards_message);
   }

   @Override
   public View getView() {
      return this;
   }

   @Override
   protected void onDetachedFromWindow() {
      if (retrySyncCardsDialog != null) retrySyncCardsDialog.dismiss();
      super.onDetachedFromWindow();
   }
}
