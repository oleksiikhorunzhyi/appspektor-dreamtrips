package com.worldventures.dreamtrips.wallet.ui.wizard.paymentcards;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import java.util.Locale;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ProgressView;

public class SyncPaymentCardScreen extends WalletLinearLayout<SyncPaymentCardPresenter.Screen, SyncPaymentCardPresenter, SyncPaymentCardPath> implements SyncPaymentCardPresenter.Screen , ProgressView {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_progress_count_cards_sync) TextView progressCountCardsSync;
   @InjectView(R.id.progressStatusLabel) TextView progressStatusLabel;
   @InjectView(R.id.firmware_install_progress) WalletProgressWidget installProgress;

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

      installProgress.start();
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
   public void showProgress(Object o) {
      installProgress.setVisibility(VISIBLE);
   }

   @Override
   public boolean isProgressVisible() {
      return installProgress.getVisibility() == VISIBLE;
   }

   @Override
   public void hideProgress() {
      installProgress.setVisibility(INVISIBLE);
   }
}
