package com.worldventures.dreamtrips.wallet.ui.common.helper2.progress;

import android.view.View;

import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import io.techery.janet.operationsubscriber.view.ProgressView;

public class WalletProgressView<T> implements ProgressView<T> {

   private final WalletProgressWidget progressWidget;

   public WalletProgressView(WalletProgressWidget progressWidget) {
      this.progressWidget = progressWidget;
   }

   @Override
   public void showProgress(T t) {
      progressWidget.setVisibility(View.VISIBLE);
      progressWidget.start();
   }

   @Override
   public boolean isProgressVisible() {
      return progressWidget.getVisibility() != View.GONE;
   }

   @Override
   public void hideProgress() {
      progressWidget.stop();
      progressWidget.setVisibility(View.GONE);
   }
}
