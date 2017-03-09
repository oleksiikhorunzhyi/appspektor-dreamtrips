package com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate;

import android.content.DialogInterface;

public class ExplicitCancelStrategy implements CancelStrategy {
   private final DialogInterface.OnCancelListener cancelListener;

   public ExplicitCancelStrategy(DialogInterface.OnCancelListener cancelListener) {
      this.cancelListener = cancelListener;
   }

   @Override
   public boolean isCancellable() {
      return true;
   }

   @Override
   public boolean isCancellableOutside() {
      return false;
   }

   @Override
   public DialogInterface.OnCancelListener getCancelListener() {
      return cancelListener;
   }
}
