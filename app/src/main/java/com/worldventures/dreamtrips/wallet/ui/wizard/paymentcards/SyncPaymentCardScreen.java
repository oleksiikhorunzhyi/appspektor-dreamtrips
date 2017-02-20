package com.worldventures.dreamtrips.wallet.ui.wizard.paymentcards;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.SyncCardsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.WalletProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryErrorDialogView;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import java.util.Locale;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class SyncPaymentCardScreen extends WalletLinearLayout<SyncPaymentCardPresenter.Screen, SyncPaymentCardPresenter, SyncPaymentCardPath> implements SyncPaymentCardPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_progress_count_cards_sync) TextView progressCountCardsSync;
   @InjectView(R.id.tv_progress_status) TextView progressStatusLabel;
   @InjectView(R.id.firmware_install_progress) WalletProgressWidget installProgress;

   private MaterialDialog retrySyncCardsDialog = null;

   public SyncPaymentCardScreen(Context context) {
      super(context);
   }

   public SyncPaymentCardScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return false;
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @Override
   public SyncPaymentCardPresenter createPresenter() {
      return new SyncPaymentCardPresenter(getContext(), getInjector(), getPath().smartCard());
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
      progressStatusLabel.setText(String.format(Locale.US, "%d%%", percent));
   }

   @Override
   public OperationView<SyncCardsCommand> provideOperationView() {
      return new ComposableOperationView<>(
            new WalletProgressView<>(installProgress),
            new RetryErrorDialogView<SyncCardsCommand>(getContext(),
                  getString(R.string.wallet_syncing_payment_cards_fail_msg),
                  command -> presenter.onRetryCanceled(),
                  command -> presenter.finish())
      );
   }

   @Override
   protected void onDetachedFromWindow() {
      if (retrySyncCardsDialog != null) retrySyncCardsDialog.dismiss();
      super.onDetachedFromWindow();
   }
}
