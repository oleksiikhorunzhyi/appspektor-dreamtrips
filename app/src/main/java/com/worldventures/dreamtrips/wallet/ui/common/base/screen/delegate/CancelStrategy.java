package com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate;

import android.content.DialogInterface;

public interface CancelStrategy {
   boolean isCancellable();

   DialogInterface.OnCancelListener getCancelListener();
}
