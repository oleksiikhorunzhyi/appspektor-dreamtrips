package com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleErrorView;

import org.jetbrains.annotations.Nullable;

import java.net.ConnectException;
import java.net.UnknownHostException;

import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpException;
import io.techery.janet.http.exception.HttpServiceException;
import io.techery.janet.operationsubscriber.view.ErrorView;
import rx.functions.Action1;

import static com.worldventures.dreamtrips.util.JanetHttpErrorHandlingUtils.handleJanetHttpError;

public class HttpErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final Context context;
   private final Action1<T> retryAction;
   private final Action1<T> cancelAction;

   public HttpErrorViewProvider(Context context, Action1<T> retryAction, Action1<T> cancelAction) {
      this.context = context;
      this.retryAction = retryAction;
      this.cancelAction = cancelAction;
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return HttpServiceException.class;
   }

   @Override
   @Nullable
   public ErrorView<T> create(T t, Throwable throwable) {
      throwable =  throwable instanceof HttpServiceException ? throwable.getCause() : throwable;
      if (throwable instanceof JanetActionException &&
            ((JanetActionException) throwable).getAction() instanceof BaseHttpAction) {
         final BaseHttpAction action = ((BaseHttpAction) ((JanetActionException) throwable).getAction());
         final String httpErrorMessage = handleJanetHttpError(context, action, throwable, null);
         if (httpErrorMessage == null) return null;
         return new SimpleErrorView<>(context, cancelAction, httpErrorMessage);
      }
      if (throwable instanceof HttpException) {
         return new SimpleErrorView<>(context, cancelAction, context.getString(R.string.error_internal_server));
      }
      if (throwable instanceof UnknownHostException || throwable instanceof ConnectException) {
         return new ConnectionErrorView<>(context, context.getString(R.string.wallet_no_internet_connection), retryAction, cancelAction);
      }
      return null;
   }
}
