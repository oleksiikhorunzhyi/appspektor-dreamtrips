package com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate;

import android.content.DialogInterface;

public class CancelAvailableStrategy implements CancelStrategy {
   private final DialogInterface.OnCancelListener cancelListener;

   public CancelAvailableStrategy(DialogInterface.OnCancelListener cancelListener) {
      this.cancelListener = cancelListener;
   }

   @Override
   public boolean isCancellable() {
      return true;
   }

   @Override
   public DialogInterface.OnCancelListener getCancelListener() {
      return cancelListener;
   }
}
