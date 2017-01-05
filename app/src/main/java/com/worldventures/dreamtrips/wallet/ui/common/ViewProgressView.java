package com.worldventures.dreamtrips.wallet.ui.common;

import android.view.View;

import io.techery.janet.operationsubscriber.view.ProgressView;

public class ViewProgressView<T> implements ProgressView<T> {

   private final View progressView;

   public ViewProgressView(View progressView) {
      this.progressView = progressView;
   }

   @Override
   public void showProgress(T t) {
      progressView.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideProgress() {
      progressView.setVisibility(View.GONE);
   }
}
