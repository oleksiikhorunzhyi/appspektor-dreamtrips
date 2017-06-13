package com.worldventures.dreamtrips.wallet.ui.common.helper2.success;

import android.content.Context;
import android.widget.Toast;

public class SimpleToastSuccessView<T> extends ToastSuccessView<T> {

   private Context context;
   private int resIdMessage;

   public SimpleToastSuccessView(Context context, int resIdMessage) {
      this.context = context;
      this.resIdMessage = resIdMessage;
   }
   @Override
   public Toast createToast(T t) {
      return Toast.makeText(context, resIdMessage, Toast.LENGTH_SHORT);
   }
}
