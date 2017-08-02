package com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleErrorView;

import org.jetbrains.annotations.Nullable;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpException;
import io.techery.janet.http.exception.HttpServiceException;
import io.techery.janet.http.model.Response;
import io.techery.janet.operationsubscriber.view.ErrorView;
import rx.functions.Action1;

public class HttpErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final Context context;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;
   private final Action1<T> retryAction;
   private final Action1<T> cancelAction;

   public HttpErrorViewProvider(Context context, HttpErrorHandlingUtil httpErrorHandlingUtil, Action1<T> retryAction, Action1<T> cancelAction) {
      this.context = context;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
      this.retryAction = retryAction;
      this.cancelAction = cancelAction;
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return HttpServiceException.class;
   }

   @Override
   @Nullable
   public ErrorView<T> create(T t, @Nullable Throwable parentThrowable, Throwable throwable) {
      throwable = throwable instanceof HttpServiceException ? throwable.getCause() : throwable;

      boolean parentIsJanetException = parentThrowable != null && parentThrowable instanceof JanetActionException;

      if (throwable instanceof JanetActionException || parentIsJanetException) {
         if (parentIsJanetException) throwable = parentThrowable;

         final Object action = ((JanetActionException) throwable).getAction();
         final String httpErrorMessage = httpErrorHandlingUtil.handleJanetHttpError(action, throwable, null);
         if (httpErrorMessage == null) return null;
         return new SimpleErrorView<>(context, cancelAction, httpErrorMessage);
      }

      if (throwable instanceof HttpException) {
         final Response response = ((HttpException) throwable).getResponse();
         if (response != null && response.getStatus() >= 500) {
            return new SimpleErrorView<>(context, cancelAction, context.getString(R.string.error_internal_server));
         } else if (throwable.getCause() != null) {
            // if there is no response at all - it might be connection exception, try to handle cause
            throwable = throwable.getCause();
         }
      }

      if (throwable instanceof UnknownHostException || throwable instanceof ConnectException) {
         return new ConnectionErrorView<>(context, context.getString(R.string.wallet_no_internet_connection), retryAction, cancelAction);
      }

      if (throwable instanceof SocketTimeoutException) {
         return new ConnectionErrorView<>(context, context.getString(R.string.wallet_connection_timed_out), retryAction, cancelAction);
      }
      return null;
   }
}
