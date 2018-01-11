package com.worldventures.dreamtrips.modules.dtl_flow.parts.common;

import android.view.View;

import com.worldventures.wallet.ui.common.helper2.progress.ViewProgressView;

import rx.functions.Action1;

public final class ActionProgressView<T> extends ViewProgressView<T> {

   private final Action1<Boolean> onProgressAction;

   public ActionProgressView(View progressView, Action1<Boolean> onProgressAction) {
      super(progressView);
      this.onProgressAction = onProgressAction;
   }

   @Override
   public void showProgress(T progress) {
      super.showProgress(progress);
      onProgressAction.call(true);
   }

   @Override
   public void hideProgress() {
      super.hideProgress();
      onProgressAction.call(false);
   }
}
