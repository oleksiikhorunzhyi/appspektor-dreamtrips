package com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate;

import android.content.DialogInterface;

public class SimpleCancelStrategy implements CancelStrategy {
   @Override
   public boolean isCancellable() {
      return false;
   }

   @Override
   public boolean isCancellableOutside() {
      return false;
   }

   @Override
   public DialogInterface.OnCancelListener getCancelListener() {
      return null;
   }
}
