package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.util.HttpUploaderyException;

import io.techery.janet.operationsubscriber.view.ErrorView;

public class UploaderyErrorViewProvider implements ErrorViewProvider {

   public final Context context;

   public UploaderyErrorViewProvider(Context context) {
      this.context = context;
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return HttpUploaderyException.class;
   }

   @Override
   public ErrorView create(Object o, Throwable throwable) {
      return new SimpleErrorView<>(context, t1 -> {
      }, context.getString(R.string.wallet_image_uploadery_error));
   }
}
